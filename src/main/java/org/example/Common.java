package org.example;

import org.jetbrains.annotations.NotNull;

public interface Common extends Comparable<Common> {
    @Override
    int compareTo(@NotNull Common o);

    int compareTo(@NotNull Actor o);

    int compareTo(@NotNull Production o);

    String getMethod();

}
