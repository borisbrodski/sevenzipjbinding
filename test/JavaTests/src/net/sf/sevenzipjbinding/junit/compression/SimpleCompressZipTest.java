package net.sf.sevenzipjbinding.junit.compression;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import net.sf.sevenzipjbinding.ArchiveFormat;
import net.sf.sevenzipjbinding.IInArchive;
import net.sf.sevenzipjbinding.IOutCreateArchiveZip;
import net.sf.sevenzipjbinding.IOutCreateCallback;
import net.sf.sevenzipjbinding.IOutItemCallbackZip;
import net.sf.sevenzipjbinding.ISequentialInStream;
import net.sf.sevenzipjbinding.SevenZip;
import net.sf.sevenzipjbinding.SevenZipException;
import net.sf.sevenzipjbinding.junit.JUnitNativeTestBase;
import net.sf.sevenzipjbinding.junit.tools.VirtualContent;
import net.sf.sevenzipjbinding.junit.tools.VirtualContent.VirtualContentConfiguration;
import net.sf.sevenzipjbinding.util.ByteArrayStream;

import org.junit.Test;

// TODO BrB Extract into top level test helper class
class CallbackTester<E> implements InvocationHandler {
    private Set<String> methodNameSet = new HashSet<String>();
    private E proxyInstance;
    private E instance;

    @SuppressWarnings("unchecked")
    public CallbackTester(E instance) {
        this.instance = instance;

        this.proxyInstance = (E) Proxy.newProxyInstance(instance.getClass().getClassLoader(), instance.getClass()
                .getInterfaces(), this);
    }

    public int getDifferentMethodsCalled() {
        return methodNameSet.size();
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        ArrayList<String> list = new ArrayList<String>(methodNameSet);
        Collections.sort(list);
        for (String methodName : list) {
            if (stringBuilder.length() == 0) {
                stringBuilder.append(", ");
            }

            stringBuilder.append(methodName);
            stringBuilder.append("()");
        }
        return "Called methods: " + stringBuilder;
    }

    public E getInstance() {
        return proxyInstance;
    }

    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (methodNameSet.add(method.getName())) {
            System.out.println(method);
        }
        try {
            return method.invoke(instance, args);
        } catch (InvocationTargetException exception) {
            throw exception.getCause();
        }
    }
}

/**
 *
 * @author Boris Brodski
 * @version 4.65-1
 */
public class SimpleCompressZipTest extends JUnitNativeTestBase {
    private class OutCreateArchiveZip implements IOutCreateCallback<IOutItemCallbackZip> {

        public void setTotal(long total) throws SevenZipException {
            //            System.out.println("setTotal(" + total + ")");
        }

        public void setCompleted(long complete) throws SevenZipException {
            //            System.out.println("setCompleted(" + complete + ")");
        }

        public ISequentialInStream getStream(int index) {
            ByteArrayStream byteArrayStream = virtualContent.getItemStream(index);
            byteArrayStream.rewind();
            return byteArrayStream;
        }

        public void setOperationResult(boolean operationResultOk) {
            assertTrue(operationResultOk);
        }

        public IOutItemCallbackZip getOutItemCallback() throws SevenZipException {
            return new IOutItemCallbackZip() {
                public long getSize(int index) throws SevenZipException {
                    return virtualContent.getItemStream(index).getSize();
                }

                public String getPath(int index) throws SevenZipException {
                    return virtualContent.getItemPath(index);
                }

                public Integer getAttributes(int index) throws SevenZipException {
                    return null;
                }

                public boolean isDir(int index) throws SevenZipException {
                    return false;
                }

                public boolean isNtfsTime(int index) throws SevenZipException {
                    return false;
                }

                public Date getModificationTime(int index) throws SevenZipException {
                    return null;
                }

                public Date getLastAccessTime(int index) throws SevenZipException {
                    return null;
                }

                public Date getCreationTime(int index) throws SevenZipException {
                    return null;
                }
            };
        }

    }

    VirtualContent virtualContent;
    CallbackTester<OutCreateArchiveZip> callbackTesterCreateArchive = new CallbackTester<OutCreateArchiveZip>(
            new OutCreateArchiveZip());

    //    CallbackTester callbackTesterItem = new CallbackTester();

    @Test
    public void testCompressionZip() throws Exception {
        virtualContent = new VirtualContent(new VirtualContentConfiguration());
        virtualContent.fillRandomly(100, 3, 3, 100, 50, null);

        ByteArrayStream byteArrayStream = new ByteArrayStream(100000);

        IOutCreateArchiveZip outNewArchiveZip = closeLater(SevenZip.openOutArchiveZip());

        outNewArchiveZip.setLevel(5);

        assertEquals(ArchiveFormat.ZIP, outNewArchiveZip.getArchiveFormat());

        outNewArchiveZip.createArchive(byteArrayStream, virtualContent.getItemCount(),
                callbackTesterCreateArchive.getInstance());

        assertEquals(5, callbackTesterCreateArchive.getDifferentMethodsCalled());

        byteArrayStream.rewind();

        IInArchive inArchive = closeLater(SevenZip.openInArchive(ArchiveFormat.ZIP, byteArrayStream));
        virtualContent.verifyInArchive(inArchive);
    }
}
