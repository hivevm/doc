package org.hivevm.document.fo_legacy;

import java.util.HashMap;
import java.util.Map;
import org.hivevm.doc.api.Header;
import org.hivevm.doc.fo.Fo;

public class Headers {

    private final Map<Header, String> headers;

    public Headers() {
        this.headers = new HashMap<>();
    }

    /**
     * Retrieves the chapter content associated with the specified header.
     */
    public final String getChapter(Header header) {
        return headers.get(header);
    }

    /**
     * Retrieves the encoded and trimmed title of the specified header.
     */
    public final String getTitle(Header header) {
        return Fo.encode(header.getTitle().trim());
    }

    /**
     * Retrieves the complete chapter title by combining the chapter content and the encoded and
     * trimmed title associated with the specified header.
     */
    public final String getChapterTitle(Header header) {
        return getChapter(header) + getTitle(header);
    }

    public void set(Header header, String content) {
        headers.put(header, content.isEmpty() ? "" : content + " ");
    }
}
