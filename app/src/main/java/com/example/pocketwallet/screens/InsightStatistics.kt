package com.example.pocketwallet.screens
import com.example.pocketwallet.R
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.draw.rotate


@Composable
fun InsightStatistics() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xff141326))
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            // Time and Status Bar (simplified)
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "08:48",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(id = R.drawable.wifi),
                            contentDescription = "WiFi",
                            modifier = Modifier.size(width = 16.dp, height = 11.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Component1()
                    }
                }
            }

            // Income / Expense Bar
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(47.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .background(
                            brush = Brush.linearGradient(
                                0f to Color(0xffe33c3c),
                                0.71f to Color(0xffe3823c)
                            )
                        ),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("INCOME", color = Color.White, fontWeight = FontWeight.Medium)
                    Text("EXPENSE", color = Color.White, fontWeight = FontWeight.Medium)
                }
            }

            // List of Info Cards
            val infoCards = listOf(
                InfoCardData("TRANSACTION", "2:05 p.m. | Sep 01, 2022"),
                InfoCardData("CATEGORY", "Groceries"),
                InfoCardData("AMOUNT", "₹235"),
                InfoCardData("PAYMENT METHOD", "Physical Cash"),
                InfoCardData("DESCRIPTION", "Weekly groceries"),
                InfoCardData("CURRENCY", "INR (₹)")
            )

            items(infoCards) { card ->
                InfoCard(title = card.title, value = card.value)
            }

            // Icon Circles
            val iconCircles = listOf(
                IconCircleData(R.drawable.addtocart, "Groceries", Brush.linearGradient(0f to Color(0xff61d8d8), 1f to Color(0xff39c0d4))),
                IconCircleData(R.drawable.tshirt, "Apparels", Brush.linearGradient(0f to Color(0xffa858ee), 1f to Color(0xff6c40d9))),
                IconCircleData(R.drawable.monitor2, "Electronics", Brush.linearGradient(0f to Color(0xffe3b53c), 0.47f to Color(0xffe3823c))),
                IconCircleData(R.drawable.groups, "Life", Brush.linearGradient(0f to Color(0xff8efdad), 1f to Color(0xff27cc55))),
                IconCircleData(R.drawable.busfront, "Transportation", Brush.linearGradient(0f to Color(0xffe33c3c), 1f to Color(0xff950d0d))),
                IconCircleData(R.drawable.addprice, "Investments", Brush.linearGradient(0f to Color(0xffffe870), 1f to Color(0xffe3b53c))),
            )

            items(iconCircles.chunked(2)) { rowItems ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    for (item in rowItems) {
                        IconCircle(icon = painterResource(id = item.iconRes), label = item.label, gradient = item.gradient)
                    }
                }
            }
        }
    }
}

@Composable
fun InfoCard(title: String, value: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(110.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(Color(0xff1c1a2b))
            .border(BorderStroke(1.dp, Color.White.copy(alpha = 0.5f)))
            .padding(16.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.SpaceBetween) {
            Text(title, color = Color.White.copy(alpha = 0.8f), fontSize = 14.sp, fontWeight = FontWeight.Medium)
            Text(value, color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
            Divider(color = Color(0xffe3b53c))
        }
    }
}

@Composable
fun IconCircle(icon: Painter, label: String, gradient: Brush) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(50.dp)
                .clip(CircleShape)
                .background(gradient),
            contentAlignment = Alignment.Center
        ) {
            Icon(painter = icon, contentDescription = label, tint = Color.Black)
        }
        Spacer(modifier = Modifier.width(12.dp))
        Text(label, color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun Component1() {
    Box(
        modifier = Modifier
            .size(width = 11.dp, height = 5.dp)
    ) {
        Divider(
            color = Color.White,
            modifier = Modifier.fillMaxSize().rotate(-41.64f)
        )
        Divider(
            color = Color.White,
            modifier = Modifier.fillMaxSize().rotate(41.64f)
        )
    }
}

data class InfoCardData(val title: String, val value: String)
data class IconCircleData(val iconRes: Int, val label: String, val gradient: Brush)

@Preview
@Composable
fun PreviewInsightStatistics() {
    InsightStatistics()
}
