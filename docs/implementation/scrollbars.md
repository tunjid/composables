# Scrollbars

Scrollbars enable tracking a user's position in the list and fast scrolling through it.

For lazy containers, the easiest way to use them is via the `scrollbarState` extension method:

```kotlin
@Composable
fun Lazy_State.scrollbarState(
    itemsAvailable: Int,
    itemIndex: (Lazy_ItemInfo) -> Int = Lazy_ItemInfo::index,
): ScrollbarState {
    ...
}
```

Use of the scroll bar follows the following pattern:

```kotlin
@Composable
fun FastScrollbar(
    modifier: Modifier = Modifier,
    state: ScrollbarState,
    scrollInProgress: Boolean,
    orientation: Orientation,
    onThumbMoved: (Float) -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }
    Scrollbar(
        modifier = modifier,
        orientation = orientation,
        interactionSource = interactionSource,
        state = state,
        thumb = {
            FastScrollbarThumb(
                scrollInProgress = scrollInProgress,
                interactionSource = interactionSource,
                orientation = orientation,
            )
        },
        onThumbMoved = onThumbMoved,
    )
}
```

They are implemented for scrollable containers with [ScrollState], lists, grids and staggered grids.


| Composable                        |                                                                                                           |                                                                                                           |                                                                                                                         |
|-----------------------------------|-----------------------------------------------------------------------------------------------------------|-----------------------------------------------------------------------------------------------------------|-------------------------------------------------------------------------------------------------------------------------|
| Collapsing Headers and Scrollbars | ![collapsing header with scrollbars in a list](../../images/collapsing_header_fast_scroll_list_crop.gif)  | ![collapsing header with scrollbars in a grid](../../images/collapsing_header_fast_scroll_grid_crop.gif)  | ![collapsing header with scrollbars in staggered_grid](../../images/collapsing_header_fast_scroll_staggered_grid_crop.gif)   |