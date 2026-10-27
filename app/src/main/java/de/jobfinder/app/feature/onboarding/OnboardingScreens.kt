package de.jobfinder.app.feature.onboarding

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import de.jobfinder.app.R
import de.jobfinder.app.core.designsystem.JobFinderColors
import de.jobfinder.app.core.designsystem.JobFinderElevation
import de.jobfinder.app.core.designsystem.JobFinderMotion
import de.jobfinder.app.core.designsystem.JobFinderShapes
import de.jobfinder.app.core.designsystem.JobFinderSpacing
import de.jobfinder.app.core.designsystem.rememberAnimationsEnabled
import de.jobfinder.app.core.designsystem.components.PrimaryPillButton

@Composable
fun OnboardingScreen(
    viewModel: OnboardingViewModel,
    onOnboardingComplete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(state.isCompleted) {
        if (state.isCompleted) {
            onOnboardingComplete()
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFFAF9FD))
            .windowInsetsPadding(WindowInsets.statusBars)
            .navigationBarsPadding()
    ) {
        if (state.currentStep == 0) {
            OnboardingStepOne(
                onNext = { viewModel.nextStep() },
                onSkip = { viewModel.skip() }
            )
        } else {
            OnboardingStepTwo(
                preferences = state.preferences,
                onToggleFullTime = { viewModel.toggleFullTime() },
                onTogglePartTime = { viewModel.togglePartTime() },
                onToggleRemote = { viewModel.toggleRemote() },
                onBack = { viewModel.previousStep() },
                onComplete = { viewModel.completeOnboarding() },
                onSkip = { viewModel.skip() }
            )
        }
    }
}

/**
 * Onboarding Step 1: Matching reference capture (390 x 884 px)
 */
@Composable
internal fun OnboardingStepOne(
    onNext: () -> Unit,
    onSkip: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = JobFinderSpacing.lg)
            .padding(bottom = JobFinderSpacing.lg),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // TopBar: Step indicator "1 von 2"
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = JobFinderSpacing.sm),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = JobFinderShapes.Pill,
                color = Color.White.copy(alpha = 0.9f),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                ),
                shadowElevation = 1.dp
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "1 von 2",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF4B5565)
                        )
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Box(
                            modifier = Modifier
                                .size(width = 16.dp, height = 6.dp)
                                .clip(JobFinderShapes.Pill)
                                .background(JobFinderColors.PrimaryLight)
                        )
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFE2E8F0))
                        )
                    }
                }
            }
        }

        // Hero 4-Card Stack: Apple, Discord, Google, Amazon
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = JobFinderSpacing.md),
            contentAlignment = Alignment.TopCenter
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // 1. Apple Card (Green #8EE04E, 64% width)
                Surface(
                    modifier = Modifier
                        .fillMaxWidth(0.64f)
                        .height(56.dp),
                    shape = RoundedCornerShape(24.dp),
                    color = Color(0xFF8EE04E),
                    shadowElevation = 2.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color.White.copy(alpha = 0.9f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.jf_ic_apple),
                                    contentDescription = null,
                                    modifier = Modifier.size(14.dp),
                                    tint = Color(0xFF1E293B)
                                )
                            }
                            Text(
                                text = "Apple",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = Color(0xFF064E3B)
                            )
                        }
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.8f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.jf_ic_bookmark),
                                contentDescription = null,
                                modifier = Modifier.size(14.dp),
                                tint = Color(0xFF064E3B)
                            )
                        }
                    }
                }

                // 2. Discord Card (Coral #F89F95, 76% width)
                Surface(
                    modifier = Modifier
                        .fillMaxWidth(0.76f)
                        .height(56.dp)
                        .offset(y = (-28).dp),
                    shape = RoundedCornerShape(26.dp),
                    color = Color(0xFFF89F95),
                    shadowElevation = 3.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color.White.copy(alpha = 0.95f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.jf_ic_discord),
                                    contentDescription = null,
                                    modifier = Modifier.size(14.dp),
                                    tint = Color(0xFF5865F2)
                                )
                            }
                            Text(
                                text = "Discord",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = Color(0xFF4C0519)
                            )
                        }
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.8f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.jf_ic_bookmark),
                                contentDescription = null,
                                modifier = Modifier.size(14.dp),
                                tint = Color(0xFF4C0519)
                            )
                        }
                    }
                }

                // 3. Google Card (Cyan #5DC0D8, 88% width)
                Surface(
                    modifier = Modifier
                        .fillMaxWidth(0.88f)
                        .height(56.dp)
                        .offset(y = (-56).dp),
                    shape = RoundedCornerShape(28.dp),
                    color = Color(0xFF5DC0D8),
                    shadowElevation = 4.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color.White),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.jf_ic_google),
                                    contentDescription = null,
                                    modifier = Modifier.size(14.dp),
                                    tint = Color.Unspecified
                                )
                            }
                            Text(
                                text = "Google",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = Color(0xFF164E63)
                            )
                        }
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.8f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.jf_ic_bookmark),
                                contentDescription = null,
                                modifier = Modifier.size(14.dp),
                                tint = Color(0xFF164E63)
                            )
                        }
                    }
                }

                // 4. Amazon Card (Front/Main, 100% width, Brand Blue #285FCB)
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .offset(y = (-80).dp)
                        .shadow(
                            elevation = 16.dp,
                            shape = RoundedCornerShape(28.dp),
                            spotColor = Color(0x50285FCB)
                        ),
                    shape = RoundedCornerShape(28.dp),
                    color = JobFinderColors.HomeBlue
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(JobFinderSpacing.lg)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                    .size(48.dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(Color.White),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "a",
                                        style = MaterialTheme.typography.titleLarge.copy(
                                            fontWeight = FontWeight.ExtraBold,
                                            fontSize = 24.sp,
                                            color = Color(0xFF0F172A)
                                        )
                                    )
                                }
                                Column {
                                    Text(
                                        text = "Amazon",
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                    )
                                    Text(
                                        text = "Vollzeit · München & Remote",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = Color.White.copy(alpha = 0.8f)
                                        )
                                    )
                                }
                            }

                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(Color.White.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.jf_ic_bookmark),
                                    contentDescription = "Job merken",
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(JobFinderSpacing.md))

                        Text(
                            text = "Senior Fullstack Engineer",
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White,
                                fontSize = 22.sp
                            )
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "75.000 € – 92.000 € · Hybrid",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = Color.White.copy(alpha = 0.9f)
                            )
                        )

                        Spacer(modifier = Modifier.height(JobFinderSpacing.md))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = JobFinderShapes.Pill,
                                color = Color(0xFF1D3B75)
                            ) {
                                Row(
                                    modifier = Modifier.padding(start = 14.dp, end = 4.dp, top = 4.dp, bottom = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Text(
                                        text = "Details ansehen",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                    )
                                    Box(
                                        modifier = Modifier
                                            .size(28.dp)
                                            .clip(CircleShape)
                                            .background(Color.White.copy(alpha = 0.2f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(text = "↗", color = Color.White, fontSize = 12.sp)
                                    }
                                }
                            }

                            Text(
                                text = "Nächster Job →",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color.White.copy(alpha = 0.9f)
                                )
                            )
                        }
                    }
                }
            }
        }

        // Bottom Text & Action Area
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(JobFinderSpacing.md)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(JobFinderSpacing.xs)) {
                Text(
                    text = "Dein Karrierebegleiter\nfür Deutschland.",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 30.sp,
                        lineHeight = 36.sp
                    ),
                    color = Color(0xFF111827)
                )

                Text(
                    text = "Wir durchsuchen StepStone, Kleinanzeigen und die Arbeitsagentur gleichzeitig – transparent, ohne Dubletten und passgenau.",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 14.sp,
                        lineHeight = 22.sp
                    ),
                    color = Color(0xFF4B5565)
                )
            }

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(JobFinderSpacing.sm)
            ) {
                PrimaryPillButton(
                    text = "Jetzt starten",
                    onClick = onNext,
                    trailingIcon = R.drawable.jf_ic_arrow_forward,
                    modifier = Modifier.fillMaxWidth()
                )

                TextButton(onClick = onSkip) {
                    Text(
                        text = "Später einrichten",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF4B5565)
                        )
                    )
                }
            }
        }
    }
}

/**
 * Onboarding Step 2: Matching reference capture (333 x 775 px)
 * With 3 cards, playful squiggles, and floating motion.
 */
@Composable
internal fun OnboardingStepTwo(
    preferences: de.jobfinder.app.core.model.OnboardingPreferences,
    onToggleFullTime: () -> Unit,
    onTogglePartTime: () -> Unit,
    onToggleRemote: () -> Unit,
    onBack: () -> Unit,
    onComplete: () -> Unit,
    onSkip: () -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "float_center")
    val floatOffset by if (rememberAnimationsEnabled()) {
        infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = -4f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 2250, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "float_offset"
        )
    } else {
        remember { mutableFloatStateOf(0f) }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = JobFinderSpacing.lg)
            .padding(bottom = JobFinderSpacing.lg),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // TopBar: Back button + Step indicator "2 von 2"
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = JobFinderSpacing.sm),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFF8FAFC))
                    .border(1.dp, Color(0xFFE2E8F0), CircleShape)
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.jf_ic_arrow_back),
                        contentDescription = "Zurück zu Schritt 1",
                        tint = Color(0xFF475569),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Surface(
                shape = JobFinderShapes.Pill,
                color = Color(0xFFF8FAFC),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
                shadowElevation = 1.dp
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFCBD5E1))
                        )
                        Box(
                            modifier = Modifier
                                .size(width = 16.dp, height = 6.dp)
                                .clip(JobFinderShapes.Pill)
                                .background(JobFinderColors.PrimaryLight)
                        )
                    }
                    Text(
                        text = "2 von 2",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF334155),
                            fontSize = 11.sp
                        )
                    )
                }
            }
        }

        // Hero 3-Card Vector Illustration with Squiggles
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp),
            contentAlignment = Alignment.Center
        ) {
            // Background decorative squiggles
            Canvas(modifier = Modifier.fillMaxSize()) {
                val path = Path().apply {
                    moveTo(size.width * 0.15f, size.height * 0.1f)
                    cubicTo(
                        size.width * 0.25f, 0f,
                        size.width * 0.35f, size.height * 0.2f,
                        size.width * 0.45f, size.height * 0.1f
                    )
                }
                drawPath(
                    path = path,
                    color = Color(0xFFFFC83B),
                    style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
                )
            }

            // Left Card (Coral #FA6B6B, -12deg)
            Surface(
                modifier = Modifier
                    .size(width = 136.dp, height = 184.dp)
                    .offset(x = (-60).dp, y = 8.dp)
                    .rotate(-12f)
                    .border(2.dp, Color(0xFF0F172A), RoundedCornerShape(22.dp))
                    .shadow(elevation = 8.dp, shape = RoundedCornerShape(22.dp)),
                shape = RoundedCornerShape(22.dp),
                color = Color(0xFFFA6B6B)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(94.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.White)
                            .border(2.dp, Color(0xFF0F172A), RoundedCornerShape(12.dp))
                    )
                }
            }

            // Right Card (Yellow #FFC83B, +12deg)
            Surface(
                modifier = Modifier
                    .size(width = 136.dp, height = 184.dp)
                    .offset(x = 60.dp, y = 8.dp)
                    .rotate(12f)
                    .border(2.dp, Color(0xFF0F172A), RoundedCornerShape(22.dp))
                    .shadow(elevation = 8.dp, shape = RoundedCornerShape(22.dp)),
                shape = RoundedCornerShape(22.dp),
                color = Color(0xFFFFC83B)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(94.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.White)
                            .border(2.dp, Color(0xFF0F172A), RoundedCornerShape(12.dp))
                    )
                }
            }

            // Center Floating Card (Brand Blue #285FCB)
            Surface(
                modifier = Modifier
                    .size(width = 154.dp, height = 210.dp)
                    .graphicsLayer { translationY = floatOffset.dp.toPx() }
                    .border(2.dp, Color(0xFF0F172A), RoundedCornerShape(26.dp))
                    .shadow(
                        elevation = 16.dp,
                        shape = RoundedCornerShape(26.dp),
                        spotColor = Color(0x60285FCB)
                    ),
                shape = RoundedCornerShape(26.dp),
                color = JobFinderColors.HomeBlue
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(12.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(106.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.White)
                            .border(2.dp, Color(0xFF0F172A), RoundedCornerShape(12.dp))
                    )

                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Box(
                                modifier = Modifier
                                    .size(14.dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(Color.White)
                                    .border(1.dp, Color(0xFF0F172A)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = "✓", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = JobFinderColors.PrimaryLight)
                            }
                            Box(
                                modifier = Modifier
                                    .height(6.dp)
                                    .width(60.dp)
                                    .clip(JobFinderShapes.Pill)
                                    .background(Color.White.copy(alpha = 0.9f))
                            )
                        }
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Box(
                                modifier = Modifier
                                    .size(14.dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(Color.White)
                                    .border(1.dp, Color(0xFF0F172A))
                            )
                            Box(
                                modifier = Modifier
                                    .height(6.dp)
                                    .width(75.dp)
                                    .clip(JobFinderShapes.Pill)
                                    .background(Color.White.copy(alpha = 0.5f))
                            )
                        }
                    }
                }
            }
        }

        // Selection Pills & Questions
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(JobFinderSpacing.md)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(JobFinderSpacing.xs)
            ) {
                Text(
                    text = "Wonach suchst du genau?",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 26.sp
                    ),
                    color = Color(0xFF0F172A),
                    textAlign = TextAlign.Center
                )

                Text(
                    text = "Wähle deine bevorzugten Arbeits- und Beschäftigungsmodelle.",
                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp),
                    color = Color(0xFF64748B),
                    textAlign = TextAlign.Center
                )
            }

            // Interactive Selection Pills: 2-column + full-width
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(JobFinderSpacing.sm)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(JobFinderSpacing.sm)
                ) {
                    JobModelPill(
                        text = "Vollzeitstelle",
                        selected = preferences.fullTime,
                        onClick = onToggleFullTime,
                        modifier = Modifier.weight(1f),
                        iconRes = R.drawable.jf_ic_briefcase
                    )

                    JobModelPill(
                        text = "Teilzeitstelle",
                        selected = preferences.partTime,
                        onClick = onTogglePartTime,
                        modifier = Modifier.weight(1f),
                        iconRes = R.drawable.jf_ic_briefcase
                    )
                }

                JobModelPill(
                    text = "Homeoffice / Remote verfügbar",
                    selected = preferences.remote,
                    onClick = onToggleRemote,
                    modifier = Modifier.fillMaxWidth(),
                    iconRes = R.drawable.jf_ic_grid
                )
            }
        }

        // Bottom CTA
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(JobFinderSpacing.xs)
        ) {
            PrimaryPillButton(
                text = "Weiter zu den Jobs",
                onClick = onComplete,
                trailingIcon = R.drawable.jf_ic_arrow_forward,
                modifier = Modifier.fillMaxWidth()
            )

            TextButton(onClick = onSkip) {
                Text(
                    text = "Später einrichten",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF64748B)
                    )
                )
            }
        }
    }
}

@Composable
private fun JobModelPill(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    iconRes: Int? = null
) {
    val borderColor by animateColorAsState(
        targetValue = if (selected) JobFinderColors.PrimaryLight else Color(0xFFE2E8F0),
        animationSpec = JobFinderMotion.effects(),
        label = "pill_border"
    )
    val containerColor by animateColorAsState(
        targetValue = if (selected) Color(0xFFEEF4FF) else Color.White,
        animationSpec = JobFinderMotion.effects(),
        label = "pill_bg"
    )
    val contentColor by animateColorAsState(
        targetValue = if (selected) Color(0xFF1E4FB5) else Color(0xFF334155),
        animationSpec = JobFinderMotion.effects(),
        label = "pill_content"
    )

    Surface(
        modifier = modifier
            .height(52.dp)
            .clip(JobFinderShapes.Pill)
            .border(
                width = if (selected) 2.dp else 1.dp,
                color = borderColor,
                shape = JobFinderShapes.Pill
            )
            .clickable(role = Role.Checkbox, onClick = onClick)
            .semantics {
                this.selected = selected
                this.role = Role.Checkbox
            },
        shape = JobFinderShapes.Pill,
        color = containerColor,
        shadowElevation = 1.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (iconRes != null) {
                Icon(
                    painter = painterResource(id = iconRes),
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = if (selected) Color(0xFF1E4FB5) else Color(0xFF64748B)
                )
                Spacer(modifier = Modifier.width(6.dp))
            }
            Text(
                text = text,
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = if (selected) FontWeight.Bold else FontWeight.SemiBold,
                    fontSize = 13.sp
                ),
                color = contentColor
            )
            if (selected) {
                Spacer(modifier = Modifier.width(6.dp))
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(JobFinderColors.PrimaryLight)
                )
            }
        }
    }
}
