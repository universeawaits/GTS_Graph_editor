package controller.verifier;

/**
 * Verifies a graph matches characteristic
 *          a class implementing has specified.
 */
public interface Verifier {
    /**
     * @return a verity of condition to verify.
     */
    boolean verify();
}
