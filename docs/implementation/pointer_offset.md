# Pointer Offset Scroll Header

Pointer offsets are useful for manually scrolling containers with [ScrollableState], when the
pointer is already involved in another interaction. For example drag and drop or a long press.

```kotlin
@Composable
fun Modifier.pointerOffsetScroll(
    state: PointerOffsetScrollState,
) {
    ...
}
```

| Composable            |                                                                          |                                                                          |                                                                                       |
|-----------------------|--------------------------------------------------------------------------|--------------------------------------------------------------------------|---------------------------------------------------------------------------------------|
| Pointer Offset Scroll | ![pointer offset list scroll](../../images/pointer_offset_list_crop.gif) | ![pointer offset grid scroll](../../images/pointer_offset_grid_crop.gif) | ![pointer offset staggered grid](../../images/pointer_offset_staggered_grid_crop.gif) |