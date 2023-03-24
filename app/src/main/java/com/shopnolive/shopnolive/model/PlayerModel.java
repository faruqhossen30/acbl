package com.shopnolive.shopnolive.model;

import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

public class PlayerModel {
    ConstraintLayout constraintLayout;
    TextView diamondText,nameText;

    public PlayerModel(ConstraintLayout constraintLayout, TextView diamondText, TextView nameText) {
        this.constraintLayout = constraintLayout;
        this.diamondText = diamondText;
        this.nameText = nameText;
    }

    public ConstraintLayout getConstraintLayout() {
        return constraintLayout;
    }

    public void setConstraintLayout(ConstraintLayout constraintLayout) {
        this.constraintLayout = constraintLayout;
    }

    public TextView getDiamondText() {
        return diamondText;
    }

    public void setDiamondText(TextView diamondText) {
        this.diamondText = diamondText;
    }

    public TextView getNameText() {
        return nameText;
    }

    public void setNameText(TextView nameText) {
        this.nameText = nameText;
    }
}
