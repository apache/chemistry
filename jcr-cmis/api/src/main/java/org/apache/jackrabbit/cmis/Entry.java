package org.apache.jackrabbit.cmis;

import java.util.Calendar;

public interface Entry {

    public String getId();

    public String getName();

    public String getParentId();

    public String getObjectId();

    public String getObjectTypeId();

    public String getCreatedBy();

    public Calendar getCreationDate();

    public String getLastModifiedBy();

    public Calendar getLastModificationDate();

    public String getChangeToken();

    public Iterable<Entry> getChildren();

    public Iterable<Entry> getDescendants();
}
