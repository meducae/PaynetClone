package uz.gita.paynetclone.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import uz.gita.paynetclone.R

@Composable
fun StatsGridSection(isLoading: Boolean = false) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        StatCard(
            title = stringResource(R.string.stat_cashback),
            value = stringResource(R.string.zero_som),
            subValue = stringResource(R.string.stat_today_zero),
            modifier = Modifier.weight(1f),
            isLoading = isLoading
        )
        StatCard(
            title = stringResource(R.string.stat_coins),
            value = "0",
            subValue = stringResource(R.string.stat_exchange),
            modifier = Modifier.weight(1f),
            isLoading = isLoading
        )
    }
}

@Composable
@Preview
fun StatsGridSectionPreview() {
    StatsGridSection()
}
