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
        items[2] = new Item("dir1" + File.separator + "file1.txt", 
                "This file located in a directory 'dir'");

        // dir2 does have separate archive item
        items[3] = new Item("dir2" + File.separator, (byte[]) null);
        items[4] = new Item("dir2" + File.separator + "file2.txt", 
                "This file located in a directory 'dir'");
        return items;
    }

    static class Item {
        private String path;
        private byte[] content;

        Item(String path, String content) {
            this(path, content.getBytes());
        }

        Item(String path, byte[] content) {
            this.path= path;
            this.content= content;
        }

        String getPath() {
            return path;
        }

        byte[] getContent() {
            return content;
        }
    }
}
