package com.example.diplom.analytics.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.diplom.analytics.fragments.EfficiencyAnalysisFragment;
import com.example.diplom.analytics.fragments.ExpenseAnalysisFragment;
import com.example.diplom.analytics.fragments.TaxAnalysisFragment;

/**
 * Адаптер для ViewPager2, управляет фрагментами аналитических отчетов
 */
public class AnalyticsPagerAdapter extends FragmentStateAdapter {

    private static final int NUM_PAGES = 3;

    private static final int EXPENSES_ANALYSIS_PAGE = 0;
    private static final int EFFICIENCY_ANALYSIS_PAGE = 1;
    private static final int TAX_ANALYSIS_PAGE = 2;

    public AnalyticsPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case EXPENSES_ANALYSIS_PAGE:
                return new ExpenseAnalysisFragment();
            case EFFICIENCY_ANALYSIS_PAGE:
                return new EfficiencyAnalysisFragment();
            case TAX_ANALYSIS_PAGE:
                return new TaxAnalysisFragment();
            default:
                throw new IllegalArgumentException("Invalid position: " + position);
        }
    }

    @Override
    public int getItemCount() {
        return NUM_PAGES;
    }
}


