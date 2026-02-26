# Season Manager Plugin

## Card Catalog Patch Workflow

SeasonManager supports layered card catalogs so you can add or rebalance cards by patch files without rewriting the base catalog.

## Runtime Architecture (Current)

- Hardcoded ID runtime handlers are removed from active execution path.
- Legacy B/C/X ID fallback catalog path is disabled.
- Runtime behavior is expected to come from catalog-defined effects and generic runtime modifier pipeline.

Load order:
1. `cards.catalog.file`
2. `cards.catalog.append_files[]`
3. `cards.catalog.patch_files[]`
4. `cards.catalog.patch_dir` auto-discovered `*.yml` / `*.yaml` files (if `patch_auto_discover: true`)

Later files override earlier files with the same card ID.

### Config keys

`cards.catalog`:
- `file`: base catalog file
- `append_files`: always-loaded overlays
- `patch_files`: explicitly-listed patch files
- `patch_dir`: auto-discovery directory under `plugins/SeasonManager/catalogs`
- `patch_auto_discover`: enable recursive patch discovery

### Remove / disable cards in patch

You can remove existing cards by ID in patch files:

```yaml
remove:
  ids: [B-001, C-010]
```

or

```yaml
disable:
  B-001: true
  C-010: true
```

Supported keys under `remove` / `disable`: `ids`, `effects`, `all`, `blessings`, `curses`.

### Reload command

Use in-game:

`/season catalog reload`

Check effective file setup:

`/season catalog show`
