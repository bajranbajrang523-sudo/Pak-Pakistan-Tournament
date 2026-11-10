package com.example.ui.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.SliderImageEntity
import com.example.ui.theme.ClearFontFamily
import com.example.ui.theme.ElectricPurple
import com.example.ui.theme.EsportsBackground
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.OrbitronFontFamily
import com.example.ui.theme.TextPrimary
import kotlinx.coroutines.delay

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeSlider(
    sliders: List<SliderImageEntity>,
    modifier: Modifier = Modifier
) {
    if (sliders.isEmpty()) return

    val pagerState = rememberPagerState(pageCount = { sliders.size })

    // Automatic rotation every 3.5 seconds with smooth animation
    LaunchedEffect(sliders.size) {
        while (true) {
            delay(3500)
            if (sliders.isNotEmpty()) {
                val nextPage = (pagerState.currentPage + 1) % sliders.size
                pagerState.animateScrollToPage(nextPage)
            }
        }
    }

    Column(modifier = modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 6.dp)
                .clip(RoundedCornerShape(16.dp))
                .border(
                    BorderStroke(
                        1.5.dp,
                        Brush.horizontalGradient(
                            listOf(
                                com.example.ui.theme.ElectricCyan,
                                com.example.ui.theme.FireOrange
                            )
                        )
                    ),
                    RoundedCornerShape(16.dp)
                )
                .background(com.example.ui.theme.PrimaryBackground)
        ) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 9f)
            ) { page ->
                val item = sliders[page]
                val drawableId = resolveSliderDrawable(item.drawableResName)

                Box(modifier = Modifier.fillMaxSize()) {
                    Image(
                        painter = painterResource(id = drawableId),
                        contentDescription = item.title,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )

                    // Cinematic Dark Navy Gradient overlay for maximum text sharpness
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colorStops = arrayOf(
                                        0.0f to Color.Transparent,
                                        0.45f to Color.Transparent,
                                        1.0f to Color(0xF5080D1A)
                                    )
                                )
                            )
                    )

                    // Crisp Glowing Tag Badge top-right
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(10.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(
                                Brush.horizontalGradient(
                                    listOf(com.example.ui.theme.PurpleAccent, com.example.ui.theme.FireOrange)
                                )
                            )
                            .border(0.8.dp, Color.White.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = item.tag,
                            fontFamily = ClearFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp,
                            color = Color.White,
                            letterSpacing = 0.5.sp
                        )
                    }

                    // Content bottom overlay with glowing title and clean subtitle
                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(14.dp)
                    ) {
                        Text(
                            text = item.title,
                            fontFamily = OrbitronFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = com.example.ui.theme.ElectricCyan,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = item.subtitle,
                            fontFamily = ClearFontFamily,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 12.sp,
                            color = com.example.ui.theme.TextWhite,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

            // Indicator dots centered at bottom with Fire Esports gradient
            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 6.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(sliders.size) { index ->
                    val isSelected = pagerState.currentPage == index
                    val dotWidth by animateDpAsState(targetValue = if (isSelected) 24.dp else 6.dp, label = "dotWidth")
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 3.dp)
                            .height(5.dp)
                            .width(dotWidth)
                            .clip(CircleShape)
                            .background(if (isSelected) com.example.ui.theme.ElectricCyan else Color.White.copy(alpha = 0.35f))
                    )
                }
            }
        }
    }
}

fun resolveSliderDrawable(name: String): Int {
    return when (name) {
        "img_pak_championship" -> R.drawable.img_pak_championship
        "ic_pak_tournament_logo" -> R.drawable.ic_pak_tournament_logo
        "img_ff_battle" -> R.drawable.img_ff_battle
        "img_ff_characters" -> R.drawable.img_ff_characters
        "img_esports_tourney" -> R.drawable.img_esports_tourney
        "img_gaming_champ" -> R.drawable.img_gaming_champ
        "ic_pro_genius_logo" -> R.drawable.ic_pro_genius_logo
        else -> R.drawable.img_pak_championship
    }
}
