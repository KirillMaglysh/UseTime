package ru.mksoft.android.use.time.use.time.use.time.motivator.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import ru.mksoft.android.use.time.use.time.use.time.motivator.R;
import ru.mksoft.android.use.time.use.time.use.time.motivator.databinding.FragmentHomeBinding;
import ru.mksoft.android.use.time.use.time.use.time.motivator.model.dao.DbHelperFactory;

import java.sql.SQLException;
import java.util.Random;

public class HomeFragment extends Fragment {
    //public class HomeFragment extends Fragment implements StatsProcessor.StatsProcessedListener{
    private FragmentHomeBinding binding;
    //    private ConstraintLayout progressBar;
    private static final String QUOTE_RESOURCE_NAME_BEGIN = "quote";

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);

        int quoteNumber = getResources().getInteger(R.integer.quotesNumber);
        String name = QUOTE_RESOURCE_NAME_BEGIN + new Random().nextInt(quoteNumber);
        binding.motivationQuote.setText(getResources().getIdentifier(name,
                "string", getContext().getPackageName()));


        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
//        ((MainActivity) getContext()).getStatsProcessor().unsubscribeUIListener();
        binding = null;
    }
}
