package com.networkedcapital.rep.presentation.goals

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.networkedcapital.rep.domain.model.Goal
import com.networkedcapital.rep.domain.model.BarChartData

@Composable
fun GoalListItem(
    goal: Goal,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 4.dp, horizontal = 0.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(81.dp)
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Mini Bar Chart
            MiniBarChart(
                data = goal.chartData.takeLast(4),
                quota = goal.quota,
                modifier = Modifier
                    .width((4 * 24 + 3 * 6).dp)
                    .height(81.dp)
                    .padding(vertical = 4.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(goal.title, fontSize = 16.sp, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
                if (goal.subtitle.isNotBlank()) {
                    Text(goal.subtitle, fontSize = 14.sp, color = Color.Gray)
                }
                Text(
                    "${goal.progressPercent.toInt()}% [${goal.typeName}]",
                    fontSize = 12.sp,
                    color = Color.Black
                )
            }
        }
    }
}

@Composable
fun MiniBarChart(
    data: List<BarChartData>,
    quota: Double,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.Bottom
    ) {
        data.forEachIndexed { idx, bar ->
            val quotaValue = if (quota > 0) quota else 1.0
            val percent = (bar.value / quotaValue).coerceIn(0.0, 1.0)
            val barHeight = (percent * 77f).dp
            Box(
                modifier = Modifier
                    .width(24.dp)
                    .height(barHeight)
                    .background(Color(0xFF8CC55D), RoundedCornerShape(3.dp))
            )
            if (idx < data.lastIndex) Spacer(modifier = Modifier.width(6.dp))
        }
    }
}

// No changes needed. Use as GoalListItem in goals screens.
