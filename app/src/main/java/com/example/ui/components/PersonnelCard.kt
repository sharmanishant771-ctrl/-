package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.Dns
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Personnel
import com.example.data.model.PersonnelCategory
import com.example.ui.theme.ActiveGreen
import com.example.ui.theme.PoliceGold
import com.example.ui.theme.PoliceNavy700
import com.example.ui.theme.PoliceNavy800
import com.example.ui.theme.SurveillanceCyan

@Composable
fun PersonnelCard(
    personnel: Personnel,
    isUnlocked: Boolean,
    onClick: () -> Unit,
    onCallClick: () -> Unit,
    onFavoriteToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    val displayMobile = personnel.getDisplayMobile(isUnlocked)
    val displayCug = personnel.getDisplayCug(isUnlocked)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
            .testTag("personnel_card_${personnel.id}"),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = androidx.compose.ui.graphics.SolidColor(
                if (personnel.isFavorite) PoliceGold.copy(alpha = 0.5f)
                else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
            ),
            width = if (personnel.isFavorite) 1.5.dp else 1.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                // Category Avatar Icon
                CategoryAvatar(category = personnel.category)

                Spacer(modifier = Modifier.width(12.dp))

                // Name, Designation, Station
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = personnel.name,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f)
                        )

                        // Favorite Icon
                        IconButton(
                            onClick = onFavoriteToggle,
                            modifier = Modifier
                                .size(32.dp)
                                .testTag("fav_btn_${personnel.id}")
                        ) {
                            Icon(
                                imageVector = if (personnel.isFavorite) Icons.Default.Star else Icons.Default.StarBorder,
                                contentDescription = if (personnel.isFavorite) "Remove favorite" else "Add favorite",
                                tint = if (personnel.isFavorite) PoliceGold else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    // Designation Badge & Text
                    Text(
                        text = personnel.designation,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.primary
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    // Thana Chip & Category Tag
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.LocationOn,
                                    contentDescription = null,
                                    modifier = Modifier.size(11.dp),
                                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = personnel.thanaName,
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }

                        // Category Pill
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = getCategoryColor(personnel.category).copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = personnel.category.badgeLabel,
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                color = getCategoryColor(personnel.category),
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Phone Numbers & Direct Call Action Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
                        shape = RoundedCornerShape(10.dp)
                    )
                    .padding(horizontal = 10.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (!isUnlocked) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = "Locked",
                                modifier = Modifier.size(13.dp),
                                tint = MaterialTheme.colorScheme.error
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                        }
                        Text(
                            text = "मो: $displayMobile",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp
                            ),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    if (personnel.cugNumber.isNotBlank()) {
                        Text(
                            text = "CUG: $displayCug",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                    }

                    if (personnel.dutyLocation.isNotBlank()) {
                        Text(
                            text = "ड्यूटी: ${personnel.dutyLocation}",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 11.sp
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                // Direct Call Button (डायरेक्ट कॉल करने की सुविधा)
                FilledIconButton(
                    onClick = onCallClick,
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = ActiveGreen,
                        contentColor = Color.White
                    ),
                    modifier = Modifier
                        .size(42.dp)
                        .testTag("direct_call_btn_${personnel.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.Call,
                        contentDescription = "डायरेक्ट कॉल करें",
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun CategoryAvatar(category: PersonnelCategory) {
    val (bgColor, iconVector) = when (category) {
        PersonnelCategory.CCTNS_OPERATOR -> Pair(SurveillanceCyan, Icons.Default.Computer)
        PersonnelCategory.CCTNS_STAFF -> Pair(Color(0xFF0D9488), Icons.Default.Build)
        PersonnelCategory.SHO_INCHARGE -> Pair(PoliceGold, Icons.Default.Shield)
        PersonnelCategory.OFFICER -> Pair(PoliceNavy800, Icons.Default.MilitaryTech)
        PersonnelCategory.CONTROL_ROOM -> Pair(Color(0xFF7C3AED), Icons.Default.Dns)
        else -> Pair(PoliceNavy700, Icons.Default.Shield)
    }

    Box(
        modifier = Modifier
            .size(46.dp)
            .clip(CircleShape)
            .background(bgColor.copy(alpha = 0.15f))
            .border(1.5.dp, bgColor.copy(alpha = 0.6f), CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = iconVector,
            contentDescription = category.englishTitle,
            tint = bgColor,
            modifier = Modifier.size(24.dp)
        )
    }
}

fun getCategoryColor(category: PersonnelCategory): Color {
    return when (category) {
        PersonnelCategory.CCTNS_OPERATOR -> SurveillanceCyan
        PersonnelCategory.CCTNS_STAFF -> Color(0xFF0D9488) // Teal
        PersonnelCategory.SHO_INCHARGE -> Color(0xFFD97706) // Amber/Gold
        PersonnelCategory.OFFICER -> PoliceNavy800
        PersonnelCategory.CONTROL_ROOM -> Color(0xFF7C3AED) // Purple
        else -> Color(0xFF475569) // Slate
    }
}
