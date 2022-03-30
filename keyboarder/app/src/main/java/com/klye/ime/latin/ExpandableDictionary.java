package com.klye.ime.latin;

import android.content.Context;
import android.os.AsyncTask;
import com.klye.ime.latin.Dictionary.DataType;
import com.klye.ime.latin.Dictionary.WordCallback;
import java.util.Iterator;
import java.util.LinkedList;

public class ExpandableDictionary extends Dictionary {
    protected static final int MAX_WORD_LENGTH = 32;
    private static final char QUOTE = '\'';
    private static char[] mWordBuilder = new char[32];
    private int[][] mCodes;
    private Context mContext;
    private int mDicTypeId;
    private int mInputLength;
    private final char[] mLookedUpString = new char[32];
    private int mMaxDepth;
    private int[] mNextLettersFrequencies;
    private boolean mRequiresReload;
    private NodeArray mRoots;
    private boolean mUpdatingDictionary;
    private Object mUpdatingLock = new Object();
    protected int ratio1 = 10;
    protected int ratio2 = 10;
    private StringBuilder sb = new StringBuilder(32);

    private class LoadDictionaryTask extends AsyncTask<Void, Void, Void> {
        private LoadDictionaryTask() {
        }

        protected Void doInBackground(Void... v) {
            ExpandableDictionary.this.loadDictionaryAsync();
            synchronized (ExpandableDictionary.this.mUpdatingLock) {
                ExpandableDictionary.this.mUpdatingDictionary = false;
            }
            return null;
        }
    }

    static class NextWord {
        int frequency;
        NextWord nextWord;
        Node word;

        NextWord(Node word, int frequency) {
            this.word = word;
            this.frequency = frequency;
        }
    }

    static class Node {
        NodeArray children;
        char code;
        int frequency;
        LinkedList<NextWord> ngrams;
        Node parent;
        boolean terminal;

        Node() {
        }
    }

    static class NodeArray {
        private static final int INCREMENT = 2;
        Node[] data = new Node[2];
        int length = 0;

        NodeArray() {
        }

        void add(Node n) {
            if (this.length + 1 > this.data.length) {
                Node[] tempData = new Node[(this.length + 2)];
                if (this.length > 0) {
                    System.arraycopy(this.data, 0, tempData, 0, this.length);
                }
                this.data = tempData;
            }
            Node[] nodeArr = this.data;
            int i = this.length;
            this.length = i + 1;
            nodeArr[i] = n;
        }
    }

    ExpandableDictionary(Context context, int dicTypeId) {
        this.mContext = context;
        clearDictionary();
        this.mCodes = new int[32][];
        this.mDicTypeId = dicTypeId;
    }

    public void loadDictionary() {
        synchronized (this.mUpdatingLock) {
            startDictionaryLoadingTaskLocked();
        }
    }

    public void startDictionaryLoadingTaskLocked() {
        if (!this.mUpdatingDictionary) {
            this.mUpdatingDictionary = true;
            this.mRequiresReload = false;
            new LoadDictionaryTask().execute(new Void[0]);
        }
    }

    public void setRequiresReload(boolean reload) {
        synchronized (this.mUpdatingLock) {
            this.mRequiresReload = reload;
        }
    }

    public boolean getRequiresReload() {
        return this.mRequiresReload;
    }

    public void loadDictionaryAsync() {
    }

    Context getContext() {
        return this.mContext;
    }

    int getMaxWordLength() {
        return 32;
    }

    public void addWord(String word, int frequency) {
        try {
            addWordRec(this.mRoots, word, 0, frequency, null);
        } catch (Throwable th) {
        }
    }

    private void addWordRec(NodeArray children, String word, int depth, int frequency, Node parentNode) {
        int wordLength = word.length();
        char c = word.charAt(depth);
        int childrenLength = children.length;
        Node childNode = null;
        boolean found = false;
        for (int i = 0; i < childrenLength; i++) {
            childNode = children.data[i];
            if (childNode.code == c) {
                found = true;
                break;
            }
        }
        if (!found) {
            childNode = new Node();
            childNode.code = c;
            childNode.parent = parentNode;
            children.add(childNode);
        }
        if (wordLength == depth + 1) {
            childNode.terminal = true;
            childNode.frequency = Math.max(frequency, childNode.frequency);
            if (childNode.frequency > 128) {
                childNode.frequency = 128;
                return;
            }
            return;
        }
        if (childNode.children == null) {
            childNode.children = new NodeArray();
        }
        addWordRec(childNode.children, word, depth + 1, frequency, childNode);
    }

    /* JADX WARNING: Missing block: B:10:0x0013, code:
            r10.mInputLength = r11.size();
            r10.mNextLettersFrequencies = r13;
     */
    /* JADX WARNING: Missing block: B:11:0x0020, code:
            if (r10.mCodes.length >= r10.mInputLength) goto L_0x0028;
     */
    /* JADX WARNING: Missing block: B:12:0x0022, code:
            r10.mCodes = new int[r10.mInputLength][];
     */
    /* JADX WARNING: Missing block: B:13:0x0028, code:
            r8 = 0;
     */
    /* JADX WARNING: Missing block: B:15:0x002b, code:
            if (r8 >= r10.mInputLength) goto L_0x003b;
     */
    /* JADX WARNING: Missing block: B:16:0x002d, code:
            r10.mCodes[r8] = r11.getCodesAt(r8);
            r8 = r8 + 1;
     */
    /* JADX WARNING: Missing block: B:21:0x003b, code:
            r10.mMaxDepth = r10.mInputLength * 3;
            getWordsRec(r10.mRoots, r11, mWordBuilder, 0, false, 1, 0, -1, r12);
            r8 = 0;
     */
    /* JADX WARNING: Missing block: B:23:0x0051, code:
            if (r8 >= r10.mInputLength) goto L_0x0011;
     */
    /* JADX WARNING: Missing block: B:24:0x0053, code:
            getWordsRec(r10.mRoots, r11, mWordBuilder, 0, false, 1, 0, r8, r12);
            r8 = r8 + 1;
     */
    /* JADX WARNING: Missing block: B:31:?, code:
            return;
     */
    public void getWords(com.klye.ime.latin.WordComposer r11, com.klye.ime.latin.Dictionary.WordCallback r12, int[] r13) {
        /*
        r10 = this;
        r6 = 1;
        r4 = 0;
        r1 = r10.mUpdatingLock;
        monitor-enter(r1);
        r0 = r10.mRequiresReload;	 Catch:{ all -> 0x0038 }
        if (r0 == 0) goto L_0x000c;
    L_0x0009:
        r10.startDictionaryLoadingTaskLocked();	 Catch:{ all -> 0x0038 }
    L_0x000c:
        r0 = r10.mUpdatingDictionary;	 Catch:{ all -> 0x0038 }
        if (r0 == 0) goto L_0x0012;
    L_0x0010:
        monitor-exit(r1);	 Catch:{ all -> 0x0038 }
    L_0x0011:
        return;
    L_0x0012:
        monitor-exit(r1);	 Catch:{ all -> 0x0038 }
        r0 = r11.size();
        r10.mInputLength = r0;
        r10.mNextLettersFrequencies = r13;
        r0 = r10.mCodes;
        r0 = r0.length;
        r1 = r10.mInputLength;
        if (r0 >= r1) goto L_0x0028;
    L_0x0022:
        r0 = r10.mInputLength;
        r0 = new int[r0][];
        r10.mCodes = r0;
    L_0x0028:
        r8 = 0;
    L_0x0029:
        r0 = r10.mInputLength;
        if (r8 >= r0) goto L_0x003b;
    L_0x002d:
        r0 = r10.mCodes;
        r1 = r11.getCodesAt(r8);
        r0[r8] = r1;
        r8 = r8 + 1;
        goto L_0x0029;
    L_0x0038:
        r0 = move-exception;
        monitor-exit(r1);	 Catch:{ all -> 0x0038 }
        throw r0;
    L_0x003b:
        r0 = r10.mInputLength;
        r0 = r0 * 3;
        r10.mMaxDepth = r0;
        r1 = r10.mRoots;
        r3 = mWordBuilder;
        r8 = -1;
        r0 = r10;
        r2 = r11;
        r5 = r4;
        r7 = r4;
        r9 = r12;
        r0.getWordsRec(r1, r2, r3, r4, r5, r6, r7, r8, r9);
        r8 = 0;
    L_0x004f:
        r0 = r10.mInputLength;
        if (r8 >= r0) goto L_0x0011;
    L_0x0053:
        r1 = r10.mRoots;
        r3 = mWordBuilder;
        r0 = r10;
        r2 = r11;
        r5 = r4;
        r7 = r4;
        r9 = r12;
        r0.getWordsRec(r1, r2, r3, r4, r5, r6, r7, r8, r9);
        r8 = r8 + 1;
        goto L_0x004f;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.klye.ime.latin.ExpandableDictionary.getWords(com.klye.ime.latin.WordComposer, com.klye.ime.latin.Dictionary$WordCallback, int[]):void");
    }

    public synchronized boolean isValidWord(CharSequence word) {
        boolean z = false;
        synchronized (this) {
            synchronized (this.mUpdatingLock) {
                if (this.mRequiresReload) {
                    startDictionaryLoadingTaskLocked();
                }
                if (this.mUpdatingDictionary) {
                } else {
                    if (getWordFrequency(word) > -1) {
                        z = true;
                    }
                }
            }
        }
        return z;
    }

    public int getWordFrequency(CharSequence word) {
        Node node = searchNode(this.mRoots, word, 0, word.length());
        return node == null ? -1 : node.frequency;
    }

    protected void getWordsRec(NodeArray roots, WordComposer codes, char[] word, int depth, boolean completion, int snr, int inputIndex, int skipPos, WordCallback callback) {
        int count = roots.length;
        int codeSize = this.mInputLength;
        if (depth <= this.mMaxDepth) {
            int[] currentChars = null;
            if (codeSize <= inputIndex) {
                completion = true;
            } else {
                currentChars = this.mCodes[inputIndex];
            }
            for (int i = 0; i < count; i++) {
                Node node = roots.data[i];
                char c = node.code;
                char lowerC = toLowerCase(c);
                boolean terminal = node.terminal;
                NodeArray children = node.children;
                int freq = node.frequency;
                if (completion) {
                    word[depth] = c;
                    if (terminal) {
                        if (!callback.addWord(word, 0, depth + 1, ((freq * snr) * this.ratio1) / this.ratio2, this.mDicTypeId, DataType.UNIGRAM)) {
                            return;
                        }
                        if (this.mNextLettersFrequencies != null && depth >= inputIndex && skipPos < 0 && this.mNextLettersFrequencies.length > word[inputIndex]) {
                            int[] iArr = this.mNextLettersFrequencies;
                            char c2 = word[inputIndex];
                            iArr[c2] = iArr[c2] + 1;
                        }
                    }
                    if (children != null) {
                        getWordsRec(children, codes, word, depth + 1, completion, snr, inputIndex, skipPos, callback);
                    }
                } else if ((c != QUOTE || currentChars[0] == 39) && depth != skipPos) {
                    int alternativesSize = skipPos >= 0 ? 1 : currentChars.length;
                    int j = 0;
                    while (j < alternativesSize) {
                        int addedAttenuation = j > 0 ? 1 : 2;
                        char currentChar = currentChars[j];
                        if (currentChar == 65535) {
                            break;
                        }
                        if (currentChar == lowerC || currentChar == c) {
                            word[depth] = c;
                            if (codeSize == inputIndex + 1) {
                                if (terminal) {
                                    int finalFreq = (freq * snr) * addedAttenuation;
                                    if (skipPos < 0) {
                                        finalFreq *= 2;
                                    }
                                    callback.addWord(word, 0, depth + 1, (this.ratio1 * finalFreq) / this.ratio2, this.mDicTypeId, DataType.UNIGRAM);
                                }
                                if (children != null) {
                                    getWordsRec(children, codes, word, depth + 1, true, snr * addedAttenuation, inputIndex + 1, skipPos, callback);
                                }
                            } else if (children != null) {
                                getWordsRec(children, codes, word, depth + 1, false, snr * addedAttenuation, inputIndex + 1, skipPos, callback);
                            }
                        }
                        j++;
                    }
                } else {
                    word[depth] = c;
                    if (children != null) {
                        getWordsRec(children, codes, word, depth + 1, completion, snr, inputIndex, skipPos, callback);
                    }
                }
            }
        }
    }

    protected int setBigram(String word1, String word2, int frequency) {
        return addOrSetBigram(word1, word2, frequency, false);
    }

    protected int addBigram(String word1, String word2, int frequency) {
        return addOrSetBigram(word1, word2, frequency, true);
    }

    private int addOrSetBigram(String word1, String word2, int frequency, boolean addFrequency) {
        try {
            Node firstWord = searchWord(this.mRoots, word1, 0, null);
            Node secondWord = searchWord(this.mRoots, word2, 0, null);
            LinkedList<NextWord> bigram = firstWord.ngrams;
            if (bigram == null || bigram.size() == 0) {
                firstWord.ngrams = new LinkedList();
                bigram = firstWord.ngrams;
            } else {
                Iterator i$ = bigram.iterator();
                while (i$.hasNext()) {
                    NextWord nw = (NextWord) i$.next();
                    if (nw.word == secondWord) {
                        if (addFrequency) {
                            nw.frequency += frequency;
                        } else {
                            nw.frequency = frequency;
                        }
                        return nw.frequency;
                    }
                }
            }
            firstWord.ngrams.add(new NextWord(secondWord, frequency));
            return frequency;
        } catch (Throwable th) {
            return 0;
        }
    }

    private Node searchWord(NodeArray children, String word, int depth, Node parentNode) {
        int wordLength = word.length();
        char c = word.charAt(depth);
        int childrenLength = children.length;
        Node childNode = null;
        for (int i = 0; i < childrenLength; i++) {
            Node node = children.data[i];
            if (node.code == c) {
                childNode = node;
                break;
            }
        }
        if (childNode == null) {
            childNode = new Node();
            childNode.code = c;
            childNode.parent = parentNode;
            children.add(childNode);
        }
        if (wordLength == depth + 1) {
            childNode.terminal = true;
            return childNode;
        }
        if (childNode.children == null) {
            childNode.children = new NodeArray();
        }
        return searchWord(childNode.children, word, depth + 1, childNode);
    }

    boolean reloadDictionaryIfRequired() {
        boolean z;
        synchronized (this.mUpdatingLock) {
            if (this.mRequiresReload) {
                startDictionaryLoadingTaskLocked();
            }
            z = this.mUpdatingDictionary;
        }
        return z;
    }

    private void runReverseLookUp(CharSequence previousWord, WordCallback callback) {
        try {
            Node prevWord = searchWord(this.mRoots, previousWord.toString(), 0, null);
            if (prevWord != null && prevWord.ngrams != null) {
                reverseLookUp(prevWord.ngrams, callback);
            }
        } catch (Throwable th) {
        }
    }

    public void getBigrams(WordComposer codes, CharSequence previousWord, WordCallback callback, int[] nextLettersFrequencies) {
        if (!reloadDictionaryIfRequired()) {
            runReverseLookUp(previousWord, callback);
        }
    }

    void waitForDictionaryLoading() {
        while (this.mUpdatingDictionary) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
            }
        }
    }

    private void reverseLookUp(LinkedList<NextWord> terminalNodes, WordCallback callback) {
        Iterator i$ = terminalNodes.iterator();
        while (i$.hasNext()) {
            NextWord nextWord = (NextWord) i$.next();
            Node node = nextWord.word;
            int freq = nextWord.frequency;
            int index = 32;
            do {
                index--;
                this.mLookedUpString[index] = node.code;
                node = node.parent;
            } while (node != null);
            callback.addWord(this.mLookedUpString, index, 32 - index, freq, this.mDicTypeId, DataType.BIGRAM);
        }
    }

    private Node searchNode(NodeArray children, CharSequence word, int offset, int length) {
        int count = children.length;
        char currentChar = word.charAt(offset);
        for (int j = 0; j < count; j++) {
            Node node = children.data[j];
            if (node.code == currentChar) {
                if (offset == length - 1) {
                    if (node.terminal) {
                        return node;
                    }
                } else if (node.children != null) {
                    Node returnNode = searchNode(node.children, word, offset + 1, length);
                    if (returnNode != null) {
                        return returnNode;
                    }
                } else {
                    continue;
                }
            }
        }
        return null;
    }

    protected void clearDictionary() {
        this.mRoots = new NodeArray();
    }

    static char toLowerCase(char c) {
        c = (char) BinaryDictionary.toAccentLess(c);
        if (c >= 'A' && c <= 'Z') {
            return (char) (c | 32);
        }
        if (c > 127) {
            return Character.toLowerCase(c);
        }
        return c;
    }
}
