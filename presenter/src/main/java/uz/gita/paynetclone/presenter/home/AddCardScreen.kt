package uz.gita.paynetclone.presenter.home

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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
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
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey
import uz.gita.paynetclone.presenter.R
import uz.gita.paynetclone.presenter.navigation.LocalAppNavigator

class AddCardScreen : Screen {
    override val key: ScreenKey = "home.add_card"

    @Composable
    override fun Content() {
        AddCardScreenContent()
    }
}

@Composable
fun AddCardScreenContent() {
    val navigator = LocalAppNavigator.current
    var cardNumber by remember { mutableStateOf("") }
    var expiryDate by remember { mutableStateOf("") }
    var cardName by remember { mutableStateOf("") }
    var selectedColorIndex by remember { mutableIntStateOf(0) }
    val cardColors = remember {
        listOf(
            Color(0xFF00B7C7),
            Color(0xFF11A857),
            Color(0xFF7B1FD3),
            Color(0xFFE09A00),
            Color(0xFFE13E3D),
            Color(0xFF2D3544),
            Color(0xFF0677C9)
        )
    }
    val isContinueEnabled by remember(cardNumber, expiryDate, cardName) {
        derivedStateOf {
            cardNumber.length == CARD_NUMBER_LENGTH &&
                expiryDate.length == EXPIRY_LENGTH &&
                cardName.isNotBlank()
        }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentWindowInsets = WindowInsets(0),
        containerColor = Color.White
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .statusBarsPadding()
                .navigationBarsPadding()
                .imePadding()
                .padding(horizontal = 22.dp)
        ) {
            AddCardTopBar(
                title = stringResource(R.string.add_card_title),
                onBackClick = navigator::back
            )

            Spacer(modifier = Modifier.height(34.dp))

            CardPreview(
                color = cardColors[selectedColorIndex],
                cardNumber = cardNumber,
                expiryDate = expiryDate,
                onCardNumberChange = { value ->
                    cardNumber = value.filter(Char::isDigit).take(CARD_NUMBER_LENGTH)
                },
                onExpiryChange = { value ->
                    expiryDate = value.filter(Char::isDigit).take(EXPIRY_LENGTH)
                }
            )

            Spacer(modifier = Modifier.height(18.dp))

            CardNameSection(
                cardName = cardName,
                onCardNameChange = { value ->
                    cardName = value.take(CARD_NAME_MAX_LENGTH)
                }
            )

            Spacer(modifier = Modifier.height(18.dp))

            ColorPicker(
                colors = cardColors,
                selectedIndex = selectedColorIndex,
                onColorSelected = { selectedColorIndex = it }
            )

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {},
                enabled = isContinueEnabled,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(76.dp)
                    .padding(bottom = 16.dp),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF00C853),
                    contentColor = Color.White,
                    disabledContainerColor = Color(0xFFE8FAF1),
                    disabledContentColor = Color(0xFFB7E1D2)
                )
            ) {
                Text(
                    text = stringResource(R.string.continue_btn),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
private fun AddCardTopBar(
    title: String,
    onBackClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = onBackClick,
            modifier = Modifier.size(46.dp)
        ) {
            BackArrowIcon()
        }
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = title,
            color = Color(0xFF15171C),
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun CardPreview(
    color: Color,
    cardNumber: String,
    expiryDate: String,
    onCardNumberChange: (String) -> Unit,
    onExpiryChange: (String) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(292.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(
                Brush.linearGradient(
                    colors = listOf(color.copy(alpha = 0.78f), color),
                    start = Offset.Zero,
                    end = Offset.Infinite
                )
            )
            .padding(18.dp)
    ) {
        AddCardInput(
            value = cardNumber,
            onValueChange = onCardNumberChange,
            hint = stringResource(R.string.card_number_hint),
            modifier = Modifier
                .fillMaxWidth()
                .height(78.dp)
                .align(Alignment.TopCenter),
            keyboardType = KeyboardType.Number,
            trailingIcon = {
                ScanCardIcon(
                    modifier = Modifier.size(32.dp),
                    contentDescription = stringResource(R.string.card_scan_content_description)
                )
            }
        )

        AddCardInput(
            value = expiryDate,
            onValueChange = onExpiryChange,
            hint = stringResource(R.string.expiry_date_hint),
            modifier = Modifier
                .width(164.dp)
                .height(80.dp)
                .align(Alignment.TopStart)
                .padding(top = 116.dp),
            keyboardType = KeyboardType.Number
        )
    }
}

@Composable
private fun CardNameSection(
    cardName: String,
    onCardNameChange: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 12.dp,
                shape = RoundedCornerShape(18.dp),
                ambientColor = Color(0x12000000),
                spotColor = Color(0x12000000)
            )
            .clip(RoundedCornerShape(18.dp))
            .background(Color.White)
            .padding(16.dp)
    ) {
        AddCardInput(
            value = cardName,
            onValueChange = onCardNameChange,
            hint = stringResource(R.string.card_name_hint),
            modifier = Modifier
                .fillMaxWidth()
                .height(78.dp),
            keyboardType = KeyboardType.Text
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = stringResource(
                R.string.characters_limit,
                cardName.length,
                CARD_NAME_MAX_LENGTH
            ),
            color = Color(0xFF545B66),
            fontSize = 18.sp
        )
    }
}

@Composable
private fun AddCardInput(
    value: String,
    onValueChange: (String) -> Unit,
    hint: String,
    modifier: Modifier = Modifier,
    keyboardType: KeyboardType,
    trailingIcon: (@Composable () -> Unit)? = null
) {
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(Color(0xFFEFF3F6)),
        textStyle = TextStyle(
            color = Color(0xFF20242C),
            fontSize = 22.sp,
            fontWeight = FontWeight.Medium
        ),
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        cursorBrush = SolidColor(Color(0xFF00AAB7)),
        visualTransformation = VisualTransformation.None,
        decorationBox = { innerTextField ->
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 22.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(modifier = Modifier.weight(1f)) {
                    if (value.isEmpty()) {
                        Text(
                            text = hint,
                            color = Color(0xFFAAB0B9),
                            fontSize = 22.sp
                        )
                    }
                    innerTextField()
                }
                trailingIcon?.invoke()
            }
        }
    )
}

@Composable
private fun ColorPicker(
    colors: List<Color>,
    selectedIndex: Int,
    onColorSelected: (Int) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        colors.forEachIndexed { index, color ->
            val selected = index == selectedIndex
            Box(
                modifier = Modifier
                    .size(58.dp)
                    .clip(CircleShape)
                    .border(
                        width = if (selected) 2.dp else 0.dp,
                        color = if (selected) Color(0xFF008F8D) else Color.Transparent,
                        shape = CircleShape
                    )
                    .padding(if (selected) 4.dp else 0.dp)
                    .clip(CircleShape)
                    .background(color)
                    .clickable { onColorSelected(index) }
            )
        }
    }
}

@Composable
private fun BackArrowIcon(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.size(30.dp)) {
        val strokeWidth = 3.5.dp.toPx()
        val centerY = size.height / 2f
        drawLine(
            color = Color(0xFF15171C),
            start = Offset(size.width * 0.18f, centerY),
            end = Offset(size.width * 0.86f, centerY),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round
        )
        drawLine(
            color = Color(0xFF15171C),
            start = Offset(size.width * 0.18f, centerY),
            end = Offset(size.width * 0.42f, size.height * 0.26f),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round
        )
        drawLine(
            color = Color(0xFF15171C),
            start = Offset(size.width * 0.18f, centerY),
            end = Offset(size.width * 0.42f, size.height * 0.74f),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round
        )
    }
}

@Composable
private fun ScanCardIcon(
    modifier: Modifier = Modifier,
    contentDescription: String
) {
    Canvas(modifier = modifier) {
        val strokeWidth = 2.2.dp.toPx()
        val stroke = Stroke(
            width = strokeWidth,
            cap = StrokeCap.Round,
            pathEffect = PathEffect.cornerPathEffect(8.dp.toPx())
        )
        val color = Color(0xFF555B66)
        val corner = 8.dp.toPx()
        val short = size.width * 0.28f
        val inset = strokeWidth

        drawLine(color, Offset(inset, corner), Offset(inset, short), strokeWidth, StrokeCap.Round)
        drawLine(color, Offset(corner, inset), Offset(short, inset), strokeWidth, StrokeCap.Round)
        drawLine(color, Offset(size.width - inset, corner), Offset(size.width - inset, short), strokeWidth, StrokeCap.Round)
        drawLine(color, Offset(size.width - corner, inset), Offset(size.width - short, inset), strokeWidth, StrokeCap.Round)
        drawLine(color, Offset(inset, size.height - corner), Offset(inset, size.height - short), strokeWidth, StrokeCap.Round)
        drawLine(color, Offset(corner, size.height - inset), Offset(short, size.height - inset), strokeWidth, StrokeCap.Round)
        drawLine(color, Offset(size.width - inset, size.height - corner), Offset(size.width - inset, size.height - short), strokeWidth, StrokeCap.Round)
        drawLine(color, Offset(size.width - corner, size.height - inset), Offset(size.width - short, size.height - inset), strokeWidth, StrokeCap.Round)

        drawRoundRect(
            color = color,
            topLeft = Offset(size.width * 0.28f, size.height * 0.32f),
            size = Size(size.width * 0.44f, size.height * 0.36f),
            cornerRadius = CornerRadius(corner, corner),
            style = stroke
        )
    }
}

@Composable
@Preview(showBackground = true)
fun AddCardScreenPreview() {
    AddCardScreenContent()
}

private const val CARD_NUMBER_LENGTH = 16
private const val EXPIRY_LENGTH = 4
private const val CARD_NAME_MAX_LENGTH = 15
