/**
 * Sections of the /masterclass page. Shared by the header submenu and the page's own contents list,
 * so both always point at the same anchors.
 */
export interface MasterclassSection {
  id: string;
  title: string;
  kind: 'topic' | 'example';
}

export const MASTERCLASS_PATH = '/masterclass/';

export const MASTERCLASS_SECTIONS: MasterclassSection[] = [
  { id: 'solid-archives', title: 'Solid archives: one pass, not n', kind: 'topic' },
  { id: 'performance-checklist', title: 'The performance checklist', kind: 'topic' },
  { id: 'plan-stream-account', title: 'Plan, stream, account', kind: 'topic' },
  { id: 'one-thread', title: 'One archive, one thread', kind: 'topic' },
  { id: 'extract-to-folder', title: 'ExtractToFolder — the reference extractor', kind: 'example' },
  { id: 'wrapped-archives', title: 'ExtractNestedArchive — .tar.gz in one go', kind: 'example' },
];
