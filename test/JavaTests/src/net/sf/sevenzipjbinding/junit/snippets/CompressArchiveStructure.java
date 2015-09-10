package net.sf.sevenzipjbinding.junit.snippets;

import java.io.File;
import java.util.Random;

/* BEGIN_SNIPPET(CompressArchiveStructure) */
public class CompressArchiveStructure {
    public static Item[] create() {

        //     <root>
        //     |
        //     +- info.txt
        //     +- random-100-bytes.dump
        //     +- dir1
        //     |  +- file-in-a-directory1.txt
        //     +- dir2
        //        +- file-in-a-directory2.txt

        Item[] items = new Item[5];

        items[0] = new Item("info.txt", "This is the info");

        byte[] content = new byte[100];
        new Random().nextBytes(content);
        items[1] = new Item("random-100-bytes.dump", content);

        // dir1 doesn't have separate archive item
        items[2] = new Item("dir1" + File.separator + "file1.txt", //
                "This file located in a directory 'dir'");

        // dir2 does have separate archive item
        items[3] = new Item("dir2" + File.separator, (byte[]) null);
        items[4] = new Item("dir2" + File.separator + "file2.txt", //
                "This file located in a directory 'dir'");
        return items;
    }

    static class Item {
        private String /*f*/path/**/;
        private byte[] /*f*/content/**/;

        Item(String path, String content) {
            this(path, content.getBytes());
        }

        Item(String path, byte[] content) {
            this./*f*/path/**/= path;
            this./*f*/content/**/= content;
        }

        String getPath() {
            return /*f*/path/**/;
        }

        byte[] getContent() {
            return /*f*/content/**/;
        }
    }
}
/* END_SNIPPET */
