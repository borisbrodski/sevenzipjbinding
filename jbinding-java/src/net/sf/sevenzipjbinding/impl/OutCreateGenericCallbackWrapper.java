package net.sf.sevenzipjbinding.impl;

import java.util.Date;

import net.sf.sevenzipjbinding.IOutCreateCallback;
import net.sf.sevenzipjbinding.IOutCreateCallbackGeneric;
import net.sf.sevenzipjbinding.IOutItemCallback;
import net.sf.sevenzipjbinding.ISequentialInStream;
import net.sf.sevenzipjbinding.PropID;
import net.sf.sevenzipjbinding.SevenZipException;

class OutCreateGenericCallbackWrapper implements IOutCreateCallback<IOutItemCallback> {
    class OutItemGenericCallbackWrapper implements IOutItemCallback {
        private int index;

        public OutItemGenericCallbackWrapper(int index) {
            this.index = index;
        }

        public boolean isDir() throws SevenZipException {
            return getProperty(Boolean.class, PropID.IS_FOLDER, false).booleanValue();
        }

        public boolean isAnti() throws SevenZipException {
            Boolean value = getProperty(Boolean.class, PropID.IS_ANTI, true);
            if (value == null) {
                return false;
            }
            return value.booleanValue();
        }

        public long getSize() throws SevenZipException {
            return getProperty(Long.class, PropID.SIZE, false).longValue();
        }

        public String getPath() throws SevenZipException {
            return getProperty(String.class, PropID.PATH, true);
        }

        public Integer getAttributes() throws SevenZipException {
            return getProperty(Integer.class, PropID.ATTRIBUTES, true);
        }

        public Integer getPosixAttributes() throws SevenZipException {
            return getProperty(Integer.class, PropID.POSIX_ATTRIB, true);
        }

        public Date getCreationTime() throws SevenZipException {
            return getProperty(Date.class, PropID.CREATION_TIME, true);
        }

        public Date getModificationTime() throws SevenZipException {
            return getProperty(Date.class, PropID.LAST_MODIFICATION_TIME, true);
        }

        public Date getLastAccessTime() throws SevenZipException {
            return getProperty(Date.class, PropID.LAST_ACCESS_TIME, true);
        }

        public String getUser() throws SevenZipException {
            return getProperty(String.class, PropID.USER, true);
        }

        public String getGroup() throws SevenZipException {
            return getProperty(String.class, PropID.GROUP, true);
        }

        @SuppressWarnings("unchecked")
        private <T> T getProperty(Class<T> clazz, PropID propID, boolean optional) throws SevenZipException {
            Object value = outCreateCallbackGeneric.getProperty(index, propID);
            if (!optional && value == null) {
                // TODO Test me
                throw new SevenZipException(//
                        "The property PropID." + propID
                                + " (if queried) is mandatory for all archive formats and can't be null. (index: "
                                + index + ")");
            }
            if (value != null && !clazz.isInstance(value)) {
                // TEST Test me
                throw new SevenZipException("Property " + propID + " type error: Expected " //
                        + clazz.getName() + ", got " + value.getClass().getName());

            }
            return (T) value;
        }

    }

    private IOutCreateCallbackGeneric outCreateCallbackGeneric;

    OutCreateGenericCallbackWrapper(IOutCreateCallbackGeneric outCreateCallbackGeneric) {
        this.outCreateCallbackGeneric = outCreateCallbackGeneric;
    }

    public ISequentialInStream getStream(int index) throws SevenZipException {
        return outCreateCallbackGeneric.getStream(index);
    }

    public void setOperationResult(boolean operationResultOk) throws SevenZipException {
        outCreateCallbackGeneric.setOperationResult(operationResultOk);
    }

    public void setTotal(long total) throws SevenZipException {
        outCreateCallbackGeneric.setTotal(total);
    }

    public void setCompleted(long complete) throws SevenZipException {
        outCreateCallbackGeneric.setCompleted(complete);
    }

    public IOutItemCallback getOutItemCallback(int index) throws SevenZipException {
        return new OutItemGenericCallbackWrapper(index);
    }

}
