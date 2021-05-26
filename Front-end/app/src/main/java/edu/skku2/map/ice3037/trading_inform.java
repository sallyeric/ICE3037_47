package edu.skku2.map.ice3037;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class trading_inform {
    @SerializedName("userId") private String userId;
    @SerializedName("companyName") private String companyName;
    @SerializedName("budgets") private int budgets;
    @SerializedName("check1") private boolean check1;
    @SerializedName("check2") private boolean check2;
    @SerializedName("check3") private boolean check3;

    public trading_inform(String userId, String companyName, int budget, Boolean check1, Boolean check2, Boolean check3){
        this.userId = userId;
        this.companyName = companyName;
        this.budgets = budget;
        this.check1 = check1;
        this.check2 = check2;
        this.check3 = check3;
    }
}
