package phase05.d17_capstone;

/** Provided domain failures; narrow types keep unexpected JDK/framework errors at 500, never 400/409. */
public final class CapstoneFailures {
    private CapstoneFailures() {}
    /** Null request or duplicate product lines. HTTP 400. */
    public static final class InvalidOrder extends RuntimeException {}
    /** Page/size/sort outside zero-based page, size 1..100, sort id/name/price. HTTP 400. */
    public static final class InvalidPage extends RuntimeException {}
    /** Unknown customer/product, missing or foreign order. HTTP 404. */
    public static final class NotFound extends RuntimeException {}
    /** Insufficient stock or cancel of non-NEW order. HTTP 409. */
    public static final class Conflict extends RuntimeException {}
}
