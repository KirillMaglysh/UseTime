package ru.mksoft.android.use.time.use.time.use.time.motivator.ui.home;

import android.app.usage.UsageStatsManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import ru.mksoft.android.use.time.use.time.use.time.motivator.R;
import ru.mksoft.android.use.time.use.time.use.time.motivator.databinding.FragmentHomeBinding;
import ru.mksoft.android.use.time.use.time.use.time.motivator.databinding.ShortSummaryBinding;

public class HomeFragment extends Fragment {
    private HomeViewModel homeViewModel;
    private FragmentHomeBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        binding.catStateLayout.button.setOnClickListener(view -> binding.catStateLayout.catImage.setImageResource(R.drawable.angry_64x64));

        TextView leftText = binding.catStateLayout.leftText;
        leftText.setText("75");

        ShortSummaryBinding shortSummary1 = binding.shortSummary1;
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
