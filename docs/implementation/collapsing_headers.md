 # Collapsing Header

Collapsing headers in the library are achieved with a wrapping Composable enclosing a scrollable
layout.

```kotlin
@Composable
fun CollapsingHeader(
    state: CollapsingHeaderState,
    headerContent: @Composable () -> Unit,
    body: @Composable () -> Unit,
) {
    ...
}
```

It works with any layout in the body composable that supports nested scrolling.

| Composable          |                                                                                                          |                                                                                                          |                                                                                                                             |
|---------------------|----------------------------------------------------------------------------------------------------------|----------------------------------------------------------------------------------------------------------|-----------------------------------------------------------------------------------------------------------------------------|
| Collapsing Headers  | ![collapsing header with scrollbars in a list](../../images/collapsing_header_fast_scroll_list_crop.gif) | ![collapsing header with scrollbars in a grid](../../images/collapsing_header_fast_scroll_grid_crop.gif) | ![collapsing header with scrollbars in staggered_grid](../../images/collapsing_header_fast_scroll_staggered_grid_crop.gif ) |
