package com.github.vladimirp1.profiler;

import org.aspectj.lang.Signature;

import java.util.Comparator;
import java.util.HashMap;
import java.util.stream.Collectors;

public class ProfilerImpl implements Profiler {
    class SignatureInfo {
        SignatureInfo(long count, long us) {
            this.count = count;
            this.us = us;
        }

        public long count = 0;
        public long us = 0;
    }

    private final HashMap<Signature, SignatureInfo> data = new HashMap<>();

    public void Record(Signature s, long us) {
        SignatureInfo si = data.get(s);
        if (si != null) {
            si.count += 1;
            si.us += us;
        } else {
            data.put(s, new SignatureInfo(1, us));
        }
    }

    public String GetStats() {
        return data.entrySet().stream().sorted(Comparator.comparing(ent -> ent.getKey().getName())).map(ent -> {
            long us = ent.getValue().us;
            long count = ent.getValue().count;
            return "Total " + us + " us/ Avg " + (us / count) + " us  (over " + ent.getValue().count + " runs) | " + ent.getKey().toString();
        }).collect(Collectors.joining("\n"));
    }
}
