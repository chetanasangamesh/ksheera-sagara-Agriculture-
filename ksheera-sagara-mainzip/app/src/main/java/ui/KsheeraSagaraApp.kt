package com.example.ksheera_sagara.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ksheera_sagara.R
import com.example.ksheera_sagara.data.Expense
import com.example.ksheera_sagara.data.MilkEntry
import com.example.ksheera_sagara.viewmodel.DairyViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private enum class AppPage(val title: String, val emoji: String) {
    Dashboard("Dashboard", "🏠"),
    Milk("Milk Entry", "🥛"),
    Expense("Expenses", "💰"),
    Reports("Reports", "📊"),
    Records("Records", "📋")
}

private val Meadow = Color(0xFF018A4D)
private val MeadowDark = Color(0xFF005C33)
private val Leaf = Color(0xFF73C64A)
private val MilkWhite = Color(0xFFFFF9E8)
private val Butter = Color(0xFFFFB000)
private val ButterDark = Color(0xFFCC8A00)
private val Soil = Color(0xFF9A4E15)
private val SoilLight = Color(0xFFBF6A30)
private val Ink = Color(0xFF102A22)
private val ErrorRed = Color(0xFFC62828)
private val SkyBlue = Color(0xFF286C7A)
private val PurpleAccent = Color(0xFF6B3FA0)
private val CardBg = Color(0xFFFFFDF7)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun KsheeraSagaraApp(viewModel: DairyViewModel) {
    val milkEntries by viewModel.milkEntries.collectAsState()
    val expenses by viewModel.expenses.collectAsState()
    val summary = viewModel.calculateSummary(milkEntries, expenses)

    var showIntro by remember { mutableStateOf(true) }
    var currentPage by remember { mutableStateOf(AppPage.Dashboard) }

    MaterialTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color(0xFFF0FFF6),
                            Color(0xFFFFFBEA),
                            Color(0xFFEEF6FF)
                        )
                    )
                )
        ) {
            if (showIntro) {
                IntroPage(onEnterClick = { showIntro = false })
                return@Box
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                AppHeader(
                    currentPage = currentPage,
                    onPageSelected = { currentPage = it },
                    onBackClick = { currentPage = AppPage.Dashboard }
                )

                MarqueeBanner()

                DashboardStrip(
                    income = summary.totalIncome,
                    expense = summary.totalExpense,
                    profit = summary.netProfit,
                    milkCount = milkEntries.size,
                    expenseCount = expenses.size
                )

                AnimatedVisibility(
                    visible = true,
                    enter = fadeIn(tween(400)) + slideInVertically(tween(400)) { it / 3 }
                ) {
                    when (currentPage) {
                        AppPage.Dashboard -> DashboardPage(
                            milkCount = milkEntries.size,
                            expenseCount = expenses.size,
                            onMilkClick = { currentPage = AppPage.Milk },
                            onExpenseClick = { currentPage = AppPage.Expense },
                            onReportsClick = { currentPage = AppPage.Reports },
                            onRecordsClick = { currentPage = AppPage.Records }
                        )
                        AppPage.Milk -> MilkEntryPage(
                            viewModel = viewModel,
                            onSaved = { currentPage = AppPage.Expense }
                        )
                        AppPage.Expense -> ExpenseEntryPage(
                            viewModel = viewModel,
                            onSaved = { currentPage = AppPage.Reports }
                        )
                        AppPage.Reports -> ReportsPage(
                            income = summary.totalIncome,
                            expense = summary.totalExpense,
                            profit = summary.netProfit,
                            milkCount = milkEntries.size,
                            expenseCount = expenses.size,
                            onClearClick = { viewModel.clearAllData() }
                        )
                        AppPage.Records -> RecordsPage(
                            milkEntries = milkEntries,
                            expenses = expenses,
                            onDeleteMilk = { viewModel.deleteMilkEntry(it) },
                            onDeleteExpense = { viewModel.deleteExpense(it) }
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun MarqueeBanner() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(
                Brush.horizontalGradient(
                    listOf(Color(0xFF153D2A), Color(0xFF1A5C3E), Color(0xFF153D2A))
                )
            )
            .padding(horizontal = 12.dp, vertical = 10.dp)
    ) {
        Text(
            text = "🐄 Fresh milk records  •  🌾 Feed & medicine expenses  •  📈 Dairy profit & loss  •  🚜 Grow your farm with clear numbers  •",
            modifier = Modifier
                .fillMaxWidth()
                .basicMarquee(iterations = Int.MAX_VALUE),
            color = Color(0xFFB8F0D2),
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun IntroPage(onEnterClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF0A3D28), Color(0xFF1B6B45), Color(0xFF0A4E6E))
                )
            )
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Spacer(Modifier.height(20.dp))

        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(Color(0xFFFFB000)),
            contentAlignment = Alignment.Center
        ) {
            Text("🐄", fontSize = 40.sp)
        }

        Text(
            text = "WELCOME TO",
            color = Color(0xFF90E8B8),
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 4.sp,
            textAlign = TextAlign.Center
        )
        Text(
            text = "KSHEERA\nSAGARA",
            color = Color.White,
            fontSize = 42.sp,
            fontWeight = FontWeight.Black,
            textAlign = TextAlign.Center,
            lineHeight = 48.sp
        )
        Text(
            text = "Dairy Farming Profit & Loss Calculator",
            color = Color(0xFFFFD580),
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0D4D32)),
            elevation = CardDefaults.cardElevation(defaultElevation = 16.dp),
            shape = RoundedCornerShape(20.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.ksheera_sagara_intro_small),
                contentDescription = "Ksheera Sagara dairy farm intro image",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(320.dp)
                    .padding(12.dp)
                    .clip(RoundedCornerShape(14.dp)),
                contentScale = ContentScale.Fit
            )
        }

        Column(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            FeaturePill("🥛", "Track milk income & fat percentage")
            FeaturePill("🌿", "Log feed, medicine & labor costs")
            FeaturePill("📊", "View live profit & loss reports")
            FeaturePill("🗄️", "All records saved on your device")
        }

        Button(
            onClick = onEnterClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(58.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFB000)),
            shape = RoundedCornerShape(14.dp)
        ) {
            Text(
                "Enter Dashboard  →",
                fontSize = 18.sp,
                fontWeight = FontWeight.Black,
                color = Color(0xFF1A1A00)
            )
        }
        Spacer(Modifier.height(12.dp))
    }
}

@Composable
private fun FeaturePill(emoji: String, text: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White.copy(alpha = 0.10f))
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(emoji, fontSize = 22.sp)
        Text(text, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun AppHeader(
    currentPage: AppPage,
    onPageSelected: (AppPage) -> Unit,
    onBackClick: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        shape = RoundedCornerShape(18.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            Color(0xFF002B1F),
                            Color(0xFF005C33),
                            Color(0xFF008A50),
                            Color(0xFFFFB000)
                        )
                    )
                )
                .padding(16.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (currentPage != AppPage.Dashboard) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.18f))
                                .clickable { onBackClick() },
                            contentAlignment = Alignment.Center
                        ) {
                            Text("←", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        }
                        Spacer(Modifier.width(10.dp))
                    } else {
                        FarmBadge()
                        Spacer(Modifier.width(12.dp))
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (currentPage == AppPage.Dashboard) "KSHEERA SAGARA" else currentPage.emoji + "  " + currentPage.title,
                            color = Color.White,
                            fontSize = if (currentPage == AppPage.Dashboard) 26.sp else 22.sp,
                            fontWeight = FontWeight.Black
                        )
                        Text(
                            text = if (currentPage == AppPage.Dashboard)
                                "Smart Dairy Profit & Loss"
                            else
                                "Tap ← to return to Dashboard",
                            color = Color(0xFFFFF5BD),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    AppPage.values().forEach { page ->
                        val selected = page == currentPage
                        val bgColor by animateColorAsState(
                            targetValue = if (selected) Color.White else Color.White.copy(alpha = 0.12f),
                            animationSpec = tween(250),
                            label = "tab_color"
                        )
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(bgColor)
                                .clickable { onPageSelected(page) }
                                .padding(vertical = 9.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(page.emoji, fontSize = 14.sp)
                                Text(
                                    text = page.title,
                                    fontSize = 9.sp,
                                    fontWeight = if (selected) FontWeight.Black else FontWeight.Medium,
                                    color = if (selected) Ink else Color.White,
                                    maxLines = 1,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FarmBadge() {
    Box(
        modifier = Modifier
            .size(52.dp)
            .clip(CircleShape)
            .background(
                Brush.radialGradient(listOf(Color(0xFFFFD580), Color(0xFFFFB000)))
            )
            .border(2.dp, Color.White.copy(alpha = 0.5f), CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Text("🐄", fontSize = 26.sp)
    }
}

@Composable
private fun DashboardStrip(
    income: Double,
    expense: Double,
    profit: Double,
    milkCount: Int,
    expenseCount: Int
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
            SummaryTile(
                emoji = "💵",
                title = "Income",
                value = formatMoney(income),
                gradient = listOf(Color(0xFF018A4D), Color(0xFF02C574)),
                modifier = Modifier.weight(1f)
            )
            SummaryTile(
                emoji = "🧾",
                title = "Expenses",
                value = formatMoney(expense),
                gradient = listOf(Color(0xFF7C3800), Color(0xFFBF6030)),
                modifier = Modifier.weight(1f)
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
            SummaryTile(
                emoji = if (profit >= 0) "📈" else "📉",
                title = if (profit >= 0) "Net Profit" else "Net Loss",
                value = formatMoney(profit),
                gradient = if (profit >= 0)
                    listOf(Color(0xFF005C33), Color(0xFF00A65A))
                else
                    listOf(Color(0xFF8B0000), Color(0xFFC62828)),
                modifier = Modifier.weight(1f)
            )
            SummaryTile(
                emoji = "📋",
                title = "Records",
                value = "$milkCount 🥛 · $expenseCount 💰",
                gradient = listOf(Color(0xFF1A5272), Color(0xFF286C7A)),
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun SummaryTile(
    emoji: String,
    title: String,
    value: String,
    gradient: List<Color>,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.shadow(6.dp, RoundedCornerShape(14.dp)),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        shape = RoundedCornerShape(14.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.linearGradient(gradient))
                .padding(14.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(emoji, fontSize = 18.sp)
                    Text(
                        title,
                        color = Color.White.copy(alpha = 0.90f),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Text(
                    value,
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Black,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun DashboardPage(
    milkCount: Int,
    expenseCount: Int,
    onMilkClick: () -> Unit,
    onExpenseClick: () -> Unit,
    onReportsClick: () -> Unit,
    onRecordsClick: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        ImageFeatureCard(
            title = "Smart Dairy Dashboard",
            subtitle = "Track milk sales, animal care costs, and daily farm profit in one clean place.",
            art = FarmArt.Farm
        )

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
            ActionCard(
                emoji = "🥛",
                title = "Add Milk",
                subtitle = "Cow & buffalo income",
                gradientColors = listOf(Color(0xFF018A4D), Color(0xFF02C574)),
                onClick = onMilkClick,
                modifier = Modifier.weight(1f)
            )
            ActionCard(
                emoji = "💰",
                title = "Add Expense",
                subtitle = "Feed, medicine & labor",
                gradientColors = listOf(Color(0xFF7C3800), Color(0xFFBF6030)),
                onClick = onExpenseClick,
                modifier = Modifier.weight(1f)
            )
        }

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
            GradientButton(
                emoji = "📊",
                text = "Profit / Loss Report",
                gradient = listOf(Color(0xFF184D34), Color(0xFF005C33)),
                onClick = onReportsClick,
                modifier = Modifier.weight(1f)
            )
            GradientButton(
                emoji = "📋",
                text = "View All Records",
                gradient = listOf(Color(0xFF1A3D72), Color(0xFF286C7A)),
                onClick = onRecordsClick,
                modifier = Modifier.weight(1f)
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFFEFFAF4))
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("Total Records Saved", fontSize = 13.sp, color = Color(0xFF4A6B58), fontWeight = FontWeight.SemiBold)
                Text("${milkCount + expenseCount} entries in database", fontSize = 12.sp, color = Color(0xFF6E9070))
            }
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                StatusBadge("$milkCount", "🥛", Color(0xFF018A4D))
                StatusBadge("$expenseCount", "💰", Color(0xFF7C3800))
            }
        }
    }
}

@Composable
private fun StatusBadge(count: String, emoji: String, color: Color) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(color.copy(alpha = 0.12f))
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Text(emoji, fontSize = 16.sp)
        Text(count, fontSize = 16.sp, fontWeight = FontWeight.Black, color = color)
    }
}

@Composable
private fun GradientButton(
    emoji: String,
    text: String,
    gradient: List<Color>,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Brush.linearGradient(gradient))
            .clickable(onClick = onClick)
            .padding(vertical = 14.dp, horizontal = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(emoji, fontSize = 16.sp)
            Text(
                text,
                color = Color.White,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun ActionCard(
    emoji: String,
    title: String,
    subtitle: String,
    gradientColors: List<Color>,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.shadow(5.dp, RoundedCornerShape(16.dp)),
        onClick = onClick,
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        shape = RoundedCornerShape(16.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.linearGradient(gradientColors))
                .padding(16.dp)
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.20f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(emoji, fontSize = 28.sp)
                }
                Text(title, color = Color.White, fontWeight = FontWeight.Black, fontSize = 16.sp, textAlign = TextAlign.Center)
                Text(subtitle, color = Color.White.copy(alpha = 0.80f), fontSize = 11.sp, textAlign = TextAlign.Center)
            }
        }
    }
}

@Composable
private fun ImageFeatureCard(title: String, subtitle: String, art: FarmArt) {
    Card(
        colors = CardDefaults.cardColors(containerColor = CardBg),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            FarmIllustration(art, Modifier.fillMaxWidth().height(170.dp))
            Column(Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp)) {
                Text(title, color = Ink, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold)
                Spacer(Modifier.height(4.dp))
                Text(subtitle, color = Color(0xFF59645A), fontSize = 13.sp)
            }
        }
    }
}

@Composable
private fun MilkEntryPage(viewModel: DairyViewModel, onSaved: () -> Unit) {
    var liters by remember { mutableStateOf("") }
    var fat by remember { mutableStateOf("") }
    var snf by remember { mutableStateOf("") }
    var rate by remember { mutableStateOf("") }

    EntryShell(
        emoji = "🥛",
        title = "Milk Collection",
        subtitle = "Record cow or buffalo milk and calculate income automatically.",
        gradientColors = listOf(Color(0xFF018A4D), Color(0xFF02A562))
    ) {
        StyledNumberField(liters, { liters = it }, "Liters Collected", "🥛")
        StyledNumberField(fat, { fat = it }, "Fat (%)", "🧪")
        StyledNumberField(snf, { snf = it }, "SNF (%)", "🔬")
        StyledNumberField(rate, { rate = it }, "Rate per Liter (Rs.)", "💵")

        val litersVal = liters.toDoubleOrNull() ?: 0.0
        val rateVal = rate.toDoubleOrNull() ?: 0.0
        if (litersVal > 0 && rateVal > 0) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFF018A4D).copy(alpha = 0.10f))
                    .padding(12.dp)
            ) {
                Text(
                    text = "Estimated Income: ${formatMoney(litersVal * rateVal)}",
                    color = Color(0xFF005C33),
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
            }
        }

        GradientButton(
            emoji = "💾",
            text = "Save Milk Entry",
            gradient = listOf(Color(0xFF018A4D), Color(0xFF02C574)),
            onClick = {
                viewModel.addMilkEntry(
                    liters = litersVal,
                    fat = fat.toDoubleOrNull() ?: 0.0,
                    snf = snf.toDoubleOrNull() ?: 0.0,
                    ratePerLiter = rateVal
                )
                liters = ""; fat = ""; snf = ""; rate = ""
                onSaved()
            },
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun ExpenseEntryPage(viewModel: DairyViewModel, onSaved: () -> Unit) {
    var category by remember { mutableStateOf("Feed") }
    var amount by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }

    val categories = listOf("Feed", "Medicine", "Labor", "Vet", "Transport", "Other")

    EntryShell(
        emoji = "💰",
        title = "Farm Expenses",
        subtitle = "Log feed, medicine, vet, labor, transport or other dairy costs.",
        gradientColors = listOf(Color(0xFF7C3800), Color(0xFFBF6030))
    ) {
        Text("Quick Category Select", fontSize = 13.sp, color = Color(0xFF5E5E5E), fontWeight = FontWeight.SemiBold)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            categories.forEach { cat ->
                val selected = cat == category
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(if (selected) Soil else Color(0xFFF0E8E0))
                        .clickable { category = cat }
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        cat,
                        fontSize = 11.sp,
                        color = if (selected) Color.White else Color(0xFF5E3B1C),
                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }

        OutlinedTextField(
            value = category,
            onValueChange = { category = it },
            label = { Text("Category") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Soil,
                focusedLabelColor = Soil
            )
        )
        StyledNumberField(amount, { amount = it }, "Amount (Rs.)", "💵")

        OutlinedTextField(
            value = note,
            onValueChange = { note = it },
            label = { Text("Note (optional)") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Soil,
                focusedLabelColor = Soil
            )
        )

        GradientButton(
            emoji = "💾",
            text = "Save Expense",
            gradient = listOf(Color(0xFF7C3800), Color(0xFFBF6030)),
            onClick = {
                viewModel.addExpense(
                    category = category.ifBlank { "General" },
                    amount = amount.toDoubleOrNull() ?: 0.0,
                    note = note
                )
                amount = ""; note = ""
                onSaved()
            },
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun ReportsPage(
    income: Double,
    expense: Double,
    profit: Double,
    milkCount: Int,
    expenseCount: Int,
    onClearClick: () -> Unit
) {
    var showConfirm by remember { mutableStateOf(false) }

    EntryShell(
        emoji = "📊",
        title = "Profit / Loss Report",
        subtitle = "Your dairy business position based on all saved records.",
        gradientColors = listOf(Color(0xFF184D34), Color(0xFF005C33))
    ) {
        ReportCard(
            rows = listOf(
                Triple("🥛", "Total milk income", formatMoney(income)),
                Triple("💸", "Total farm expense", formatMoney(expense)),
                Triple("📋", "Milk records", milkCount.toString()),
                Triple("📋", "Expense records", expenseCount.toString()),
            )
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(
                    if (profit >= 0)
                        Brush.linearGradient(listOf(Color(0xFF018A4D), Color(0xFF02C574)))
                    else
                        Brush.linearGradient(listOf(Color(0xFF8B0000), Color(0xFFC62828)))
                )
                .padding(18.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = if (profit >= 0) "🎉 Net Profit" else "⚠️ Net Loss",
                        color = Color.White.copy(alpha = 0.90f),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = formatMoney(profit),
                        color = Color.White,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Black
                    )
                }
                Text(if (profit >= 0) "📈" else "📉", fontSize = 42.sp)
            }
        }

        if (!showConfirm) {
            OutlinedButton(
                onClick = { showConfirm = true },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, ErrorRed)
            ) {
                Text("🗑  Clear All Data", color = ErrorRed, fontWeight = FontWeight.Bold)
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFFFF0F0))
                    .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    "⚠️ This will permanently delete ALL milk and expense records. Are you sure?",
                    fontSize = 13.sp,
                    color = ErrorRed,
                    fontWeight = FontWeight.SemiBold
                )
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedButton(
                        onClick = { showConfirm = false },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Cancel", fontWeight = FontWeight.Bold)
                    }
                    Button(
                        onClick = { onClearClick(); showConfirm = false },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = ErrorRed),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Yes, Delete", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun ReportCard(rows: List<Triple<String, String, String>>) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5FBF7)),
        elevation = CardDefaults.cardElevation(2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            rows.forEachIndexed { i, (emoji, title, value) ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(emoji, fontSize = 16.sp)
                        Text(title, color = Color(0xFF5E685F), fontWeight = FontWeight.Medium, fontSize = 14.sp)
                    }
                    Text(value, color = Ink, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
                if (i < rows.lastIndex) {
                    HorizontalDivider(color = Color(0xFFE0EDE4), thickness = 0.8.dp)
                }
            }
        }
    }
}

@Composable
private fun RecordsPage(
    milkEntries: List<MilkEntry>,
    expenses: List<Expense>,
    onDeleteMilk: (Int) -> Unit,
    onDeleteExpense: (Int) -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Card(
            colors = CardDefaults.cardColors(containerColor = CardBg),
            elevation = CardDefaults.cardElevation(4.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.linearGradient(listOf(Color(0xFF1A3D72), Color(0xFF286C7A)))
                        )
                        .padding(16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text("📋", fontSize = 28.sp)
                        Column {
                            Text("Database Records", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Black)
                            Text("All your saved milk and expense entries", color = Color.White.copy(0.80f), fontSize = 12.sp)
                        }
                    }
                }

                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = Color(0xFFF0F8FF),
                    contentColor = Color(0xFF1A3D72),
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                            color = Color(0xFF286C7A),
                            height = 3.dp
                        )
                    }
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = {
                            Text(
                                "🥛 Milk (${milkEntries.size})",
                                fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 13.sp
                            )
                        }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = {
                            Text(
                                "💰 Expenses (${expenses.size})",
                                fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 13.sp
                            )
                        }
                    )
                }
            }
        }

        if (selectedTab == 0) {
            if (milkEntries.isEmpty()) {
                EmptyState("🥛", "No milk entries yet", "Add your first milk collection record")
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    milkEntries.forEach { entry ->
                        MilkEntryCard(entry = entry, onDelete = { onDeleteMilk(entry.id) })
                    }
                }
            }
        } else {
            if (expenses.isEmpty()) {
                EmptyState("💰", "No expenses yet", "Add your first farm expense record")
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    expenses.forEach { expense ->
                        ExpenseCard(expense = expense, onDelete = { onDeleteExpense(expense.id) })
                    }
                }
            }
        }
    }
}

@Composable
private fun MilkEntryCard(entry: MilkEntry, onDelete: () -> Unit) {
    var showConfirm by remember { mutableStateOf(false) }

    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF0FFF6)),
        elevation = CardDefaults.cardElevation(3.dp),
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF018A4D).copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("🥛", fontSize = 20.sp)
                    }
                    Column {
                        Text(
                            formatDate(entry.date),
                            fontSize = 12.sp,
                            color = Color(0xFF6E9070),
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            formatMoney(entry.totalAmount),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFF018A4D)
                        )
                    }
                }
                if (!showConfirm) {
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(ErrorRed.copy(alpha = 0.10f))
                            .clickable { showConfirm = true }
                            .padding(8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("🗑", fontSize = 16.sp)
                    }
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                InfoChip("${entry.liters}L", Color(0xFF018A4D))
                InfoChip("Fat: ${entry.fat}%", Color(0xFF286C7A))
                InfoChip("SNF: ${entry.snf}%", Color(0xFF6B3FA0))
                InfoChip("Rs.${entry.ratePerLiter}/L", Color(0xFF7C3800))
            }

            if (showConfirm) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedButton(
                        onClick = { showConfirm = false },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Keep", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                    Button(
                        onClick = onDelete,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = ErrorRed),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Delete", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun ExpenseCard(expense: Expense, onDelete: () -> Unit) {
    var showConfirm by remember { mutableStateOf(false) }
    val categoryColor = categoryColor(expense.category)

    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF8F0)),
        elevation = CardDefaults.cardElevation(3.dp),
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(categoryColor.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(categoryEmoji(expense.category), fontSize = 20.sp)
                    }
                    Column {
                        Text(
                            formatDate(expense.date),
                            fontSize = 12.sp,
                            color = Color(0xFF9A7050),
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            formatMoney(expense.amount),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black,
                            color = categoryColor
                        )
                    }
                }
                if (!showConfirm) {
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(ErrorRed.copy(alpha = 0.10f))
                            .clickable { showConfirm = true }
                            .padding(8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("🗑", fontSize = 16.sp)
                    }
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                InfoChip(expense.category, categoryColor)
                if (expense.note.isNotBlank()) {
                    InfoChip("📝 ${expense.note}", Color(0xFF5E5E5E))
                }
            }

            if (showConfirm) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedButton(
                        onClick = { showConfirm = false },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Keep", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                    Button(
                        onClick = onDelete,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = ErrorRed),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Delete", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun InfoChip(text: String, color: Color) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(color.copy(alpha = 0.12f))
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(text, fontSize = 11.sp, color = color, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun EmptyState(emoji: String, title: String, subtitle: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFFF5F8FA))
            .padding(40.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(emoji, fontSize = 48.sp)
            Text(title, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF4A5E55), textAlign = TextAlign.Center)
            Text(subtitle, fontSize = 13.sp, color = Color(0xFF8A9E90), textAlign = TextAlign.Center)
        }
    }
}

@Composable
private fun EntryShell(
    emoji: String,
    title: String,
    subtitle: String,
    gradientColors: List<Color>,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = CardBg),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(18.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Brush.linearGradient(gradientColors))
                    .padding(18.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.20f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(emoji, fontSize = 26.sp)
                    }
                    Column {
                        Text(title, color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Black)
                        Text(subtitle, color = Color.White.copy(0.80f), fontSize = 12.sp)
                    }
                }
            }
            Column(
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                content = content
            )
        }
    }
}

@Composable
private fun StyledNumberField(value: String, onValueChange: (String) -> Unit, label: String, emoji: String) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text("$emoji  $label") },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        shape = RoundedCornerShape(12.dp)
    )
}

private enum class FarmArt { Cow, Buffalo, Farm }

@Composable
private fun FarmIllustration(art: FarmArt, modifier: Modifier = Modifier) {
    val image = when (art) {
        FarmArt.Cow -> R.drawable.real_dairy_cow
        FarmArt.Buffalo -> R.drawable.real_dairy_buffalo
        FarmArt.Farm -> R.drawable.real_dairy_farm
    }
    Image(
        painter = painterResource(id = image),
        contentDescription = art.name,
        modifier = modifier
            .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
            .background(Color(0xFFEAF5DD)),
        contentScale = ContentScale.Crop
    )
}

private fun categoryColor(category: String): Color = when (category.lowercase()) {
    "feed" -> Color(0xFF3D7A00)
    "medicine" -> Color(0xFF0060A8)
    "labor" -> Color(0xFF7B3500)
    "vet" -> Color(0xFF005E8A)
    "transport" -> Color(0xFF6B3FA0)
    else -> Color(0xFF5E5E5E)
}

private fun categoryEmoji(category: String): String = when (category.lowercase()) {
    "feed" -> "🌾"
    "medicine" -> "💊"
    "labor" -> "👷"
    "vet" -> "🩺"
    "transport" -> "🚛"
    else -> "📦"
}

private fun formatDate(millis: Long): String =
    SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()).format(Date(millis))

private fun formatMoney(value: Double): String = "Rs. %.2f".format(value)

private fun DrawScope.drawSkyAndField() {
    drawRect(
        brush = Brush.verticalGradient(listOf(Color(0xFFBFE4F2), Color(0xFFEAF5DD))),
        size = size
    )
    drawCircle(Color(0xFFFFD66B), radius = size.minDimension * 0.12f, center = Offset(size.width * 0.82f, size.height * 0.22f))
    drawRoundRect(
        color = Color(0xFF8FCB62),
        topLeft = Offset(0f, size.height * 0.58f),
        size = Size(size.width, size.height * 0.42f),
        cornerRadius = CornerRadius(18f, 18f)
    )
}

private fun DrawScope.drawCow() {
    val w = size.width
    val h = size.height
    drawRoundRect(Color.White, Offset(w * 0.22f, h * 0.43f), Size(w * 0.48f, h * 0.25f), CornerRadius(26f, 26f))
    drawCircle(Color.White, radius = h * 0.13f, center = Offset(w * 0.71f, h * 0.45f))
    drawCircle(Color.Black, radius = h * 0.045f, center = Offset(w * 0.38f, h * 0.51f))
    drawCircle(Color.Black, radius = h * 0.05f, center = Offset(w * 0.53f, h * 0.57f))
    drawCircle(Color.Black, radius = h * 0.018f, center = Offset(w * 0.75f, h * 0.42f))
    drawRect(Soil, Offset(w * 0.29f, h * 0.66f), Size(w * 0.05f, h * 0.18f))
    drawRect(Soil, Offset(w * 0.58f, h * 0.66f), Size(w * 0.05f, h * 0.18f))
    drawRoundRect(Color(0xFFFFD2C2), Offset(w * 0.67f, h * 0.48f), Size(w * 0.15f, h * 0.09f), CornerRadius(18f, 18f))
    drawLine(Color(0xFF3E2A17), Offset(w * 0.23f, h * 0.49f), Offset(w * 0.13f, h * 0.36f), strokeWidth = 5f)
}

private fun DrawScope.drawBuffalo() {
    val w = size.width
    val h = size.height
    drawRoundRect(Color(0xFF3C3C3C), Offset(w * 0.19f, h * 0.45f), Size(w * 0.55f, h * 0.25f), CornerRadius(28f, 28f))
    drawCircle(Color(0xFF303030), radius = h * 0.14f, center = Offset(w * 0.73f, h * 0.46f))
    drawLine(Color(0xFFE8D8A8), Offset(w * 0.66f, h * 0.35f), Offset(w * 0.54f, h * 0.25f), strokeWidth = 7f)
    drawLine(Color(0xFFE8D8A8), Offset(w * 0.80f, h * 0.35f), Offset(w * 0.93f, h * 0.25f), strokeWidth = 7f)
    drawCircle(Color.White, radius = h * 0.018f, center = Offset(w * 0.76f, h * 0.42f))
    drawRect(Color(0xFF252525), Offset(w * 0.30f, h * 0.68f), Size(w * 0.06f, h * 0.17f))
    drawRect(Color(0xFF252525), Offset(w * 0.60f, h * 0.68f), Size(w * 0.06f, h * 0.17f))
    drawLine(Color(0xFF252525), Offset(w * 0.20f, h * 0.52f), Offset(w * 0.10f, h * 0.40f), strokeWidth = 6f)
}

private fun DrawScope.drawFarmScene() {
    val w = size.width
    val h = size.height
    val roof = Path().apply {
        moveTo(w * 0.18f, h * 0.50f)
        lineTo(w * 0.36f, h * 0.28f)
        lineTo(w * 0.54f, h * 0.50f)
        close()
    }
    drawPath(roof, Color(0xFFB84A2A))
    drawRoundRect(Color(0xFFFFF3D4), Offset(w * 0.23f, h * 0.49f), Size(w * 0.26f, h * 0.26f), CornerRadius(8f, 8f))
    drawRect(Soil, Offset(w * 0.33f, h * 0.61f), Size(w * 0.08f, h * 0.14f))
    drawLine(Leaf, Offset(w * 0.62f, h * 0.75f), Offset(w * 0.88f, h * 0.55f), strokeWidth = 7f)
    drawLine(Leaf, Offset(w * 0.63f, h * 0.83f), Offset(w * 0.91f, h * 0.63f), strokeWidth = 7f)
    drawLine(Leaf, Offset(w * 0.59f, h * 0.66f), Offset(w * 0.83f, h * 0.47f), strokeWidth = 7f)
    drawCircle(Color.White, radius = h * 0.055f, center = Offset(w * 0.70f, h * 0.46f))
    drawCircle(Color(0xFF303030), radius = h * 0.05f, center = Offset(w * 0.78f, h * 0.48f))
}
