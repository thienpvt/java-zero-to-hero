package phase05.d17_capstone;

import java.util.List;

/** Zero-based page metadata. Service enforces size 1–100 and stable sort. */
public record PageResponse<T>(List<T> items, int page, int size, long totalItems, int totalPages) {}
