package Anon.client.service.core.authentication;

/**
 * This class will be extended by client or controller classes in the authentication module
 */
public abstract class Id {
    // assume this is some long and hashed value.
    // unique for sure.
    private String id;

    // this method generates a random id,
    // still need to verify its uniqueness
    public static String generateId() {
        return "temporaryId";
    }
}
