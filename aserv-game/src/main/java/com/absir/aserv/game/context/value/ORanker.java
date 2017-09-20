package com.absir.aserv.game.context.value;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by absir on 19/9/17.
 */
public abstract class ORanker<R, O> {

    private int maxSize;

    private List<R> ranks;

    private LinkedList<R> rankings = new LinkedList<R>();

    protected abstract int getScore(R rank);

    protected abstract void setScore(R rank, int score);

    protected abstract boolean hasOther(R rank, O other);

    protected abstract R newRank(O other, int score);

    public ORanker(int maxSize) {
        this.maxSize = maxSize;
    }

    public List<R> getRanks() {
        if (ranks == null) {
            synchronized (rankings) {
                ranks = new ArrayList<R>(rankings);
            }
        }

        return ranks;
    }

    public void onRank(O other, int score) {
        // 快速筛选无效数据
        if (rankings.size() >= maxSize) {
            if (getScore(rankings.getLast()) >= score) {
                return;
            }
        }

        synchronized (rankings) {
            int i = -1;
            int iI = -1;
            int hI = -1;
            for (R rank : rankings) {
                i++;
                if (iI < 0) {
                    if (score > getScore(rank)) {
                        iI = i;
                        if (hI >= 0) {
                            break;
                        }
                    }
                }
                if (hI < 0) {
                    if (hasOther(rank, other)) {
                        hI = i;
                        if (iI >= 0) {
                            break;
                        }
                    }
                }
            }

            if (hI < 0) {
                if (iI >= 0) {
                    R rank = newRank(other, score);
                    rankings.add(iI, rank);
                    addRank(rank);
                    if (rankings.size() > maxSize) {
                        removeRank(rankings.removeLast());
                    }

                    ranks = null;

                } else if (rankings.size() < maxSize) {
                    R rank = newRank(other, score);
                    rankings.add(rank);
                    addRank(rank);
                    ranks = null;
                }

            } else {
                if (iI >= 0) {
                    R rank = rankings.remove(hI);
                    rankings.add(iI, rank);
                    ranks = null;
                }
            }
        }
    }

    public void clear() {
        synchronized (rankings) {
            rankings.clear();
            ranks = null;
            clearAllRank();
        }
    }

    protected abstract void addRank(R rank);

    protected abstract void removeRank(R rank);

    protected abstract void clearAllRank();
}
