package org.apache.chemistry.impl.simple;

import java.util.Map;

import org.apache.chemistry.ObjectId;
import org.apache.chemistry.Rendition;

public class SimpleRendition implements Rendition {

    private final ObjectId objectId;

    private final String contentStreamId;

    private final ObjectId renditionDocumentId;

    private final String mimeType;

    private final long length;

    private final String title;

    private final String kind;

    private final long height;

    private final long width;

    private final Map<String, String> metadata;

    public SimpleRendition(ObjectId objectId, String contentStreamId,
            ObjectId renditionDocumentId, String mimeType, long length,
            String title, String kind, long height, long width,
            Map<String, String> metadata) {
        this.objectId = objectId;
        this.contentStreamId = contentStreamId;
        this.renditionDocumentId = renditionDocumentId;
        this.mimeType = mimeType;
        this.length = length;
        this.title = title;
        this.kind = kind;
        this.height = height;
        this.width = width;
        this.metadata = metadata;
    }

    public ObjectId getObjectId() {
        return objectId;
    }

    public String getContentStreamId() {
        return contentStreamId;
    }

    public ObjectId getRenditionDocumentId() {
        return renditionDocumentId;
    }

    public String getMimeType() {
        return mimeType;
    }

    public long getLength() {
        return length;
    }

    public String getTitle() {
        return title;
    }

    public String getKind() {
        return kind;
    }

    public long getHeight() {
        return height;
    }

    public long getWidth() {
        return width;
    }

    public Map<String, String> getMetadata() {
        return metadata;
    }

}
