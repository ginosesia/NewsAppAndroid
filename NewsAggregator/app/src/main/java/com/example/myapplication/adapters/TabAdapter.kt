package com.example.myapplication.adapters

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class TabAdapter (activity: AppCompatActivity) : FragmentStateAdapter(activity) {

    override fun createFragment(position: Int): Fragment {
        when (position) {
            0 -> return TrendingFragment()
            1 -> return TodayFragment()
            2 -> return FollowingFragment()
        }
        return TodayFragment()
    }

    //Return number of tabs
    override fun getItemCount(): Int {
        return 3
    }
}