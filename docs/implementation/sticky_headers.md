# Sticky Headers

Sticky headers in the library are achieved with a wrapping Composable enclosing the lazy layout.

```kotlin
@Composable
fun StickyHeader_(
    state: Lazy_State,
    modifier: Modifier = Modifier,
    isStickyHeaderItem: @DisallowComposableCalls (Lazy_ItemInfo) -> Boolean,
    stickyHeader: @Composable (index: Int, key: Any?, contentType: Any?) -> Unit,
    content: @Composable () -> Unit
) {
    ...
}
```

They are implemented for lists, grids and staggered grids.

| Composable                        |                                                   |                                                   |                                                                       |
|-----------------------------------|---------------------------------------------------|---------------------------------------------------|-----------------------------------------------------------------------|
| Sticky Headers                    | ![list](../../images/sticky_header_list_crop.gif) | ![grid](../../images/sticky_header_grid_crop.gif) | ![staggered_grid](../../images/sticky_header_staggered_grid_crop.gif) |
