package com.shopnolive.shopnolive.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

class ChatViewPagerAdapter(
    fragmentManager: FragmentManager,
    behavior: Int
): FragmentPagerAdapter(fragmentManager, behavior) {

    private var fragments = ArrayList<Fragment>()
    private var fragmentsTitle = ArrayList<String>()

    fun addFragments(fragment: Fragment, title: String) {
        fragments.add(fragment)
        fragmentsTitle.add(title)
    }

    override fun getItem(position: Int): Fragment {
        return fragments[position]
    }

    override fun getCount(): Int {
        return fragments.size
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return fragmentsTitle[position]
    }
}