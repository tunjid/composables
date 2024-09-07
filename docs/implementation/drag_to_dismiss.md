 # Drag To Dismiss

Used for easily implementing the drag to dismiss pattern for media.

```kotlin
@Composable
fun ParentLayout() {
    Child(
        modifier = Modifier
            .dragToDismiss(
                state = dragToDismissState,
                dragThresholdCheck = { offset, _ ->
                    offset.getDistanceSquared() > with(density) {
                        240.dp.toPx().let { it * it }
                    }
                },
                onDismissed = {
                    ...
                },
            )
    )
    ...
}
```


| Composable                        |                                                                                      |                                                                                                   |                                                                                            |
|-----------------------------------|--------------------------------------------------------------------------------------|---------------------------------------------------------------------------------------------------|--------------------------------------------------------------------------------------------|
| Drag To Dismiss                   | ![color drag to dismiss](../../images/drag_to_dismiss_crop.gif)                      | ![image drag to dismiss](../../images/drag_to_dismiss_app_image.gif)                              | ![video drag to dismiss](../../images/drag_to_dismiss_app_video.gif)                       |
