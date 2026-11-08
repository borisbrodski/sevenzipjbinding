# `upstream-7zip` — pristine 7-Zip vendor branch

This branch is **not** the 7-Zip-JBinding project. It holds the **pristine, unmodified 7-Zip source**
(the `7zip/` tree) and nothing else. It exists so that upgrading the bundled 7-Zip engine is a
mechanical, low-risk `git merge` instead of a manual re-port.

> The real project (Java + JNI bindings, tests, website, build) lives on **`master`**.
> Check that branch out to work on 7-Zip-JBinding.

## What's here
- `7zip/` — the exact 7-Zip source, converted to LF (this repo's convention; see `.gitattributes`).
- `.gitattributes` — kept so git normalizes new drops to LF identically to `master`.
- `update-vendor-7zip.sh` — the update helper (below).
- `.gitignore` — ignores everything except the files listed here, so stray checkout leftovers can't be committed.
- Current version: **7-Zip 26.05** (`7z2605-src.7z` from the ip7z/7zip GitHub release 26.05, 2026-10-21, sha256 `fa081a1a…`; identical tree to `7z2605-src.tar.xz`). Earlier drops: 26.03 (2026-09-03), 23.01 (2023-06-20).

## The vendor-branch idea
`master`'s `7zip/` = *this pristine tree* **+** a small set of local edits (8 files as of 23.01 —
Unix-port shims, cross-toolchain fixes, one thread-safety bug fix). Those edits are catalogued in the
vault: **`20-Development/7-Zip Source Modifications.md`**.

Because this branch is an ancestor of `master` (linked with `git merge -s ours`), upgrading works by
3-way merge: git replays *our* edits onto the *new* pristine source automatically, and only asks us
to resolve a conflict where upstream changed the very same lines we did.

## Upgrading to a new 7-Zip release
```sh
# Use a worktree: switching the main checkout to this branch would wipe untracked build output
# (website/node_modules, dist, ...) and, worse, a later `git add -A` here could sweep such leftovers
# into the drop commit (that happened with the 26.03 drop; the .gitignore now blocks it).
git worktree add ../SevenZipJBinding-upstream-7zip upstream-7zip
cd ../SevenZipJBinding-upstream-7zip
./update-vendor-7zip.sh /path/to/7zXXXX-src.7z      # or a path to an extracted source dir
#   → wipes 7zip/{Asm,C,CPP,DOC} and drops the new pristine tree in (git normalizes CRLF→LF)
git add 7zip README.md            # (edit the "Current version" line above first)
git status --short | grep -v '^[MAD] *7zip/'   # must show nothing but README.md
git commit -m "Original 7-zip XX.XX (YYYY-MM-DD) — pristine vendor drop"

cd ../SevenZipJBinding               # the main checkout (master, or the release candidate branch)
git merge --no-ff upstream-7zip   # 3-way: our edits re-apply onto the new source
#   → README.md always conflicts (this vendor README vs the project README): git checkout --ours README.md
#   → resolve only real conflicts (upstream touched a line we also edited)
#   → then re-verify each entry in "7-Zip Source Modifications.md" still applies / can be dropped
```

Do **not** hand-edit `7zip/` on this branch beyond dropping a new pristine release — keeping it pure
is the whole point. All 7-Zip-JBinding changes belong on `master`.
