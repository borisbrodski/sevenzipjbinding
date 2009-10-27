MKDIR = mkdir -p

DEST_HOME=/usr/local
DEST_BIN=$(DEST_HOME)/bin
DEST_SHARE=$(DEST_HOME)/lib/p7zip
DEST_SHARE_DOC=$(DEST_HOME)/share/doc/p7zip
DEST_MAN=$(DEST_HOME)/man

.PHONY: default all all2 7za 7zG sfx 7z 7zr Client7z common common7z clean tar_bin depend test test_7z test_7zr test_7zG test_Client7z all_test

default:7za

all:7za sfx

all2: 7za sfx 7z

all3: 7za sfx 7z 7zr

all_test : test test_7z test_7zr test_Client7z
	cd CPP/7zip/Compress/PPMD_Alone  ; $(MAKE) test
	cd CPP/7zip/Compress/LZMA_Alone  ; $(MAKE) test

common:
	$(MKDIR) bin

7za: common
	cd CPP/7zip/Bundles/Alone ; $(MAKE) all

7zr: common
	cd CPP/7zip/Bundles/Alone7z ; $(MAKE) all

Client7z: common
	$(MKDIR) bin/Codecs
	cd CPP/7zip/Bundles/Format7zFree ; $(MAKE) all
	cd CPP/7zip/UI/Client7z      ; $(MAKE) all

depend:
	cd CPP/7zip/Bundles/Alone     ; $(MAKE) depend
	cd CPP/7zip/Bundles/Alone7z   ; $(MAKE) depend
	cd CPP/7zip/Bundles/SFXCon    ; $(MAKE) depend
	cd CPP/7zip/UI/Client7z       ; $(MAKE) depend
	cd CPP/7zip/UI/Console        ; $(MAKE) depend
	cd CPP/7zip/Bundles/Format7zFree ; $(MAKE) depend
	cd CPP/7zip/Compress/Rar      ; $(MAKE) depend
	cd CPP/7zip/UI/GUI            ; $(MAKE) depend

sfx: common
	$(MKDIR) bin
	cd CPP/7zip/Bundles/SFXCon ; $(MAKE) all

common7z:common
	$(MKDIR) bin/Codecs
	cd CPP/7zip/Bundles/Format7zFree ; $(MAKE) all
	cd CPP/7zip/Compress/Rar         ; $(MAKE) all

7z: common7z
	cd CPP/7zip/UI/Console           ; $(MAKE) all

7zG: common7z
	cd bin ; rm -f Lang ; ln -s ../GUI/Lang .
	cd bin ; rm -f help ; ln -s ../GUI/help .
	cd CPP/7zip/UI/GUI               ; $(MAKE) all

clean:
	cd CPP/myWindows                 ; $(MAKE) clean
	cd CPP/7zip/Bundles/Alone        ; $(MAKE) clean
	cd CPP/7zip/Bundles/Alone7z      ; $(MAKE) clean
	cd CPP/7zip/Bundles/SFXCon       ; $(MAKE) clean
	cd CPP/7zip/UI/Client7z          ; $(MAKE) clean
	cd CPP/7zip/UI/Console           ; $(MAKE) clean
	cd CPP/7zip/UI/FileManager       ; $(MAKE) clean
	cd CPP/7zip/UI/GUI               ; $(MAKE) clean
	cd CPP/7zip/Bundles/Format7zFree ; $(MAKE) clean
	cd CPP/7zip/Compress/Rar         ; $(MAKE) clean
	cd CPP/7zip/Compress/LZMA_Alone  ; $(MAKE) clean
	cd CPP/7zip/Compress/PPMD_Alone  ; $(MAKE) clean
	cd CPP/7zip/Bundles/AloneGCOV    ; $(MAKE) clean
	rm -fr bin
	rm -fr CPP/7zip/CMAKE/Alone
	rm -f make.log 1 2
	rm -f check/7z.so
	find . -name "*~" -exec rm -f {} \;
	find . -name "*.orig" -exec rm -fr {} \;
	find . -name ".*.swp" -exec rm -f {} \;
	find . -name "*.[ch]" -exec chmod -x {} \;
	find . -name "*.cpp" -exec chmod -x {} \;
	find . -name "*.asm" -exec chmod -x {} \;
	find . -name "makefile*" -exec chmod -x {} \;
	chmod -x ChangeLog README TODO man1/* DOCS/*.txt
	chmod +x contrib/VirtualFileSystemForMidnightCommander/u7z
	chmod +x contrib/gzip-like_CLI_wrapper_for_7z/p7zip
	chmod +x install.sh check/check.sh check/clean_all.sh check/check_7zr.sh 
	cd check                  ; ./clean_all.sh

test: 7za sfx
	cd check ; ./check.sh ../bin/7za

test_7z: 7z sfx
	cd check ; ./check.sh ../bin/7z

test_7zr: 7zr sfx
	cd check ; ./check_7zr.sh ../bin/7zr

test_7zG: 7zG sfx
	cd check ; ./check.sh ../bin/7zG

test_Client7z: Client7z
	cd check ; ./check_Client7z.sh ../bin/Client7z

install:
	./install.sh $(DEST_BIN) $(DEST_SHARE) $(DEST_MAN) $(DEST_SHARE_DOC) $(DEST_DIR)

REP=$(shell pwd)
ARCHIVE=$(shell basename $(REP))

.PHONY: tar_all tar_all2 src_7z tar_bin tar_bin2

tar_all : clean
	rm -f  ../$(ARCHIVE)_src_all.tar.bz2
	cp makefile.linux_x86_ppc_alpha makefile.machine
	cd .. ; (tar cf - $(ARCHIVE) | bzip2 -9 > $(ARCHIVE)_src_all.tar.bz2)

tar_all2 : clean
	rm -f  ../$(ARCHIVE)_src_all.tar.bz2
	cp makefile.linux_x86_ppc_alpha makefile.machine
	cd .. ; (tar cf - $(ARCHIVE) | 7za a -mx=9 -tbzip2 -si $(ARCHIVE)_src_all.tar.bz2 )

src_7z : clean
	rm -f  ../$(ARCHIVE)_src.7z
	cd .. ; 7za a -mx=9 -m0=ppmd:mem=128m:o=32 $(ARCHIVE)_src.7z $(ARCHIVE)

tar_bin:
	rm -f  ../$(ARCHIVE)_x86_linux_bin.tar.bz2
	chmod +x install.sh contrib/VirtualFileSystemForMidnightCommander/u7z contrib/gzip-like_CLI_wrapper_for_7z/p7zip
	cd .. ; (tar cf - $(ARCHIVE)/bin $(ARCHIVE)/contrib $(ARCHIVE)/man1 $(ARCHIVE)/install.sh $(ARCHIVE)/ChangeLog $(ARCHIVE)/DOCS $(ARCHIVE)/README $(ARCHIVE)/TODO | bzip2 -9 > $(ARCHIVE)_x86_linux_bin.tar.bz2)

tar_bin2:
	rm -f  ../$(ARCHIVE)_x86_linux_bin.tar.bz2
	chmod +x install.sh contrib/VirtualFileSystemForMidnightCommander/u7z contrib/gzip-like_CLI_wrapper_for_7z/p7zip
	cd .. ; (tar cf - $(ARCHIVE)/bin $(ARCHIVE)/contrib $(ARCHIVE)/man1 $(ARCHIVE)/install.sh $(ARCHIVE)/ChangeLog $(ARCHIVE)/DOCS $(ARCHIVE)/README $(ARCHIVE)/TODO | 7za a -mx=9 -tbzip2 -si $(ARCHIVE)_x86_linux_bin.tar.bz2)

