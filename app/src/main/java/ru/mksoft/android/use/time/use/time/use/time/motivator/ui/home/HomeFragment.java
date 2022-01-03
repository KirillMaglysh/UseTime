package ru.mksoft.android.use.time.use.time.use.time.motivator.ui.home;

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
import ru.mksoft.android.use.time.use.time.use.time.motivator.model.TrackedApp;
import ru.mksoft.android.use.time.use.time.use.time.motivator.model.dao.DbHelperFactory;

import java.sql.SQLException;
import java.util.List;

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

//        final TextView textView1 = binding.text1;
//        textView1.setText("25 hours");
        final TextView leftText = binding.catStateLayout.leftText;
        leftText.setText("75");

/*
        homeViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
*/

        List<TrackedApp> allApps = null;
        try {
            allApps = DbHelperFactory.getHelper().getTrackedAppDAO().getAllApps();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        ShortSummaryBinding shortSummary1 = binding.shortSummary1;
//        shortSummary1.currentPurposeCard.description.setText(allApps.get(0).getName());
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
