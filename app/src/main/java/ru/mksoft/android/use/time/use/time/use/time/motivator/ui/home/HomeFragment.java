package ru.mksoft.android.use.time.use.time.use.time.motivator.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import ru.mksoft.android.use.time.use.time.use.time.motivator.R;
import ru.mksoft.android.use.time.use.time.use.time.motivator.databinding.FragmentHomeBinding;
import ru.mksoft.android.use.time.use.time.use.time.motivator.databinding.ShortSummaryBinding;

public class HomeFragment extends Fragment {
    //public class HomeFragment extends Fragment implements StatsProcessor.StatsProcessedListener{
    private FragmentHomeBinding binding;
    private ConstraintLayout progressBar;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();


//        StatsProcessor statsProcessor = ((MainActivity) getContext()).getStatsProcessor();
//        progressBar = binding.shortStatsListProgressWindow.progressWindow;
//        if (statsProcessor.isPrecessed()) {
//            processStatsProcessedBuilt();
//        } else {
//            statsProcessor.subscribe(this);
//            progressBar.setVisibility(View.VISIBLE);
//        }
//
        binding.catStateLayout.button.setOnClickListener(view -> binding.catStateLayout.catImage.setImageResource(R.drawable.angry_64x64));

        TextView leftText = binding.catStateLayout.leftText;
        leftText.setText("75");

        ShortSummaryBinding shortSummary1 = binding.shortSummary1;


        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
//        ((MainActivity) getContext()).getStatsProcessor().unsubscribeUIListener();
        binding = null;
    }
}
