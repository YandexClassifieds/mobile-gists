import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.AlignmentLine
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.offset
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.HorizontalAlignmentLine
import androidx.compose.ui.layout.layout
import kotlin.math.min

val CenterHorizontalAlignmentLine = HorizontalAlignmentLine(::min)

/*
 * Badge will align to the center of the object with this modifier
 */
fun Modifier.centerHorizontalAlignmentLine() =
    layout { measurable, constraints ->
        val placeable = measurable.measure(constraints)
        layout(
            width = placeable.width,
            height = placeable.height,
            alignmentLines = mapOf(CenterHorizontalAlignmentLine to placeable.height / 2)
        ) {
            placeable.place(0, 0)
        }
    }

@Composable
fun TimelineDecorator(
    modifier: Modifier = Modifier,
    badge: @Composable (() -> Unit) = { TimelineDefaults.Badge() },
    topLine: (@Composable (() -> Unit))? = { TimelineDefaults.Line() },
    bottomLine: @Composable (() -> Unit) = { TimelineDefaults.Line() },
    lineWidth: Dp = TimelineDefaults.LineWidth,
    linePadding: PaddingValues = TimelineDefaults.LinePadding,
    content: @Composable (() -> Unit),
) {
    Layout(
        modifier = modifier,
        content = {
            topLine?.invoke() ?: run { Spacer(modifier = Modifier) }
            badge()
            bottomLine()
            content()
        },
        measurePolicy = { measurables, constraints ->
            val lineStartPaddingPx = linePadding.calculateStartPadding(layoutDirection).roundToPx()
            val lineTopPaddingPx = linePadding.calculateTopPadding().roundToPx()
            val lineEndPaddingPx = linePadding.calculateEndPadding(layoutDirection).roundToPx()
            val lineBottomPaddingPx = linePadding.calculateBottomPadding().roundToPx()
            val lineWidthPx = lineWidth.roundToPx()
            val halfLineWidthPx = (lineWidth / 2).roundToPx()

            val (topLineMeasurable, badgeMeasurable, bottomLineMeasurable, contentMeasurable) = measurables
            val lineWidthWithPaddingPx = lineWidthPx + lineStartPaddingPx + lineEndPaddingPx
            val contentPlaceable = contentMeasurable.measure(constraints.offset(horizontal = -lineWidthWithPaddingPx))

            val badgeAlignmentLineOrUnspecified = contentPlaceable[CenterHorizontalAlignmentLine]
            val badgeAlignmentLine = if (badgeAlignmentLineOrUnspecified == AlignmentLine.Unspecified) {
                contentPlaceable.height / 2
            } else {
                badgeAlignmentLineOrUnspecified
            }

            val badgePlaceable = badgeMeasurable.measure(constraints)
            val halfBadgeHeight = badgePlaceable.height / 2
            val halfBadgeWidth = badgePlaceable.width / 2

            val topLineHeight = (badgeAlignmentLine - halfBadgeHeight - lineBottomPaddingPx)
                .coerceAtLeast(0)
            val topLinePlaceable = topLineMeasurable.measure(
                Constraints.fixed(width = lineWidthPx, height = topLineHeight)
            )

            val bottomLineHeight = (contentPlaceable.height - badgeAlignmentLine - halfBadgeHeight - lineTopPaddingPx)
                .coerceAtLeast(0)
            val bottomLinePlaceable = bottomLineMeasurable.measure(
                Constraints.fixed(width = lineWidthPx, height = bottomLineHeight)
            )

            layout(
                width = lineWidthWithPaddingPx + contentPlaceable.width,
                height = contentPlaceable.height
            ) {
                topLinePlaceable.place(lineStartPaddingPx, 0)
                badgePlaceable.place(
                    lineStartPaddingPx + halfLineWidthPx - halfBadgeWidth,
                    badgeAlignmentLine - halfBadgeHeight
                )
                contentPlaceable.place(lineWidthWithPaddingPx, 0)
                bottomLinePlaceable.place(lineStartPaddingPx, contentPlaceable.height - bottomLineHeight)
            }
        }
    )
}
