package uz.gita.paynetclone.screens.home

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.hilt.getViewModel
import uz.gita.paynetclone.R
import uz.gita.paynetclone.components.PaynetCardItem
import uz.gita.paynetclone.presenter.home.MyCardsContract
import uz.gita.paynetclone.presenter.home.MyCardsViewModel
import uz.gita.paynetclone.ui.theme.PaynetCloneTheme
import uz.gita.paynetclone.ui.theme.SatoshiBold

class MyCardsScreen : Screen {
    override val key: ScreenKey = "home.my_cards"

    @Composable
    override fun Content() {
        val viewModel: MyCardsContract.ViewModel = getViewModel<MyCardsViewModel>()
        val uiState by viewModel.uiState.collectAsState()

        MyCardsScreenContent(
            uiState = uiState,
            onEventDispatcher = viewModel::onEventDispatcher
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyCardsScreenContent(
    uiState: MyCardsContract.UiState,
    onEventDispatcher: (MyCardsContract.Intent) -> Unit
) {
    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current
    val copiedMessage = stringResource(R.string.copy_to_clipboard)

    Scaffold(
        modifier = Modifier.fillMaxSize().systemBarsPadding(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text =stringResource(R.string.my_cards_title) ,
                        fontFamily = SatoshiBold,
                        fontSize = 20.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { onEventDispatcher(MyCardsContract.Intent.Back) }) {
                        Icon(
                            painter = painterResource(id = R.drawable.back),
                            contentDescription = "Back",
                            modifier = Modifier.size(24.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground,
                    navigationIconContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        },
        bottomBar = {
            Button(
                onClick = { onEventDispatcher(MyCardsContract.Intent.AddCard) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .height(56.dp),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF00964E),
                    contentColor = Color.White
                )
            ) {
                Text(
                    text = stringResource(R.string.add_card_btn),
                    fontFamily = SatoshiBold,
                    fontSize = 16.sp
                )
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (uiState.cards.isEmpty()) {
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(uiState.cards) { card ->
                    PaynetCardItem(
                        card = card,
                        modifier = Modifier.clickable {
                            onEventDispatcher(MyCardsContract.Intent.OpenDetails(card.id))
                        },
                        onCopyClick = {
                            clipboardManager.setText(AnnotatedString(it))
                            Toast.makeText(context, copiedMessage, Toast.LENGTH_SHORT).show()
                        }
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MyCardsScreenPreview() {
    PaynetCloneTheme {
        MyCardsScreenContent(
            uiState = MyCardsContract.UiState(),
            onEventDispatcher = {}
        )
    }
}
