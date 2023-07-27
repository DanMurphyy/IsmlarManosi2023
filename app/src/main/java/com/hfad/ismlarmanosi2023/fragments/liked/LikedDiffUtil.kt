package com.hfad.ismlarmanosi2023.fragments.liked

import androidx.recyclerview.widget.DiffUtil
import com.hfad.ismlarmanosi2023.dataLiked.LikedData

class LikedDiffUtil(
    private val oldList: List<LikedData>,
    private val newList: List<LikedData>,
) : DiffUtil.Callback() {
    override fun getOldListSize(): Int {
        return oldList.size
    }

    override fun getNewListSize(): Int {
        return newList.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] === newList[newItemPosition]
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].id == newList[newItemPosition].id
                &&
                oldList[oldItemPosition].name == newList[newItemPosition].name
                &&
                oldList[oldItemPosition].gender == newList[newItemPosition].gender
                &&
                oldList[oldItemPosition].meaning == newList[newItemPosition].meaning
                &&
                oldList[oldItemPosition].origin == newList[newItemPosition].origin
    }

}