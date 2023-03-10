package com.fca.xtreme;

import android.app.assist.AssistStructure;
import android.os.Build;
import android.os.CancellationSignal;
import android.service.autofill.AutofillService;
import android.service.autofill.Dataset;
import android.service.autofill.FillCallback;
import android.service.autofill.FillContext;
import android.service.autofill.FillRequest;
import android.service.autofill.FillResponse;
import android.service.autofill.SaveCallback;
import android.service.autofill.SaveRequest;
import android.view.autofill.AutofillValue;
import android.widget.RemoteViews;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import java.util.ArrayList;
import java.util.List;

@RequiresApi(api = Build.VERSION_CODES.O)
public class Autofill extends AutofillService {

    FillResponse.Builder fillResponseBuilder;
    ArrayList<Account> accounts;

    @Override
    public void onFillRequest(@NonNull FillRequest request, @NonNull CancellationSignal cancellationSignal, @NonNull FillCallback callback) {
        List<FillContext> context = request.getFillContexts();
        AssistStructure structure = context.get(context.size() - 1).getStructure();

        fillResponseBuilder = new FillResponse.Builder();

        accounts = new Helpers().readAccounts(getApplicationContext());

        traverseStructure(structure);

        try {
            callback.onSuccess(fillResponseBuilder.build());
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onSaveRequest(@NonNull SaveRequest request, @NonNull SaveCallback callback) {

    }

    public void traverseStructure(AssistStructure structure){
        int nodes = structure.getWindowNodeCount();

        for(int i = 0; i < nodes; i++) {
            AssistStructure.WindowNode windowNode = structure.getWindowNodeAt(i);
            AssistStructure.ViewNode viewNode = windowNode.getRootViewNode();
            traverseNode(viewNode);
        }
    }

    public void traverseNode(AssistStructure.ViewNode viewNode) {

        if(viewNode.getAutofillHints() != null && viewNode.getAutofillHints().length > 0) {
            // the client app provided autofill hints
            if(inputType(viewNode.getAutofillHints()).equals("username")) {
                populateResponseBuilder("username", viewNode);
            } else if(inputType(viewNode.getAutofillHints()).equals("password")) {
                populateResponseBuilder("password", viewNode);
            }
        } else if(viewNode.getHint() != null && viewNode.getHint().length() > 0) {
            // the client app haven't provided autofill hints
            if (inputType(viewNode.getHint().split(" ")).equals("username")) {
                populateResponseBuilder("username", viewNode);
            } else if (inputType(viewNode.getHint().split(" ")).equals("password")) {
                populateResponseBuilder("password", viewNode);
            }
        }

        for(int i = 0; i < viewNode.getChildCount(); i++) {
            AssistStructure.ViewNode childNode = viewNode.getChildAt(i);
            traverseNode(childNode);
        }

    }

    public void populateResponseBuilder(String query, AssistStructure.ViewNode viewNode) {

        for(Account account : accounts) {

            RemoteViews presentation = new RemoteViews(getPackageName(), android.R.layout.simple_list_item_1);
            String value;
            String presentation_value = account.getServiceName();

            if (query.equals("username")) { // is an username field
                value = account.getUsername();
                presentation_value += " (" + value + ")";
            } else {
                value = account.getPassword();
                StringBuilder sb = new StringBuilder();
                for(int i = 0; i < value.length(); i++)
                    sb.append("*");
                presentation_value += " (" + sb.toString() + ")";
            }

            presentation.setTextViewText(android.R.id.text1, presentation_value);

            Dataset.Builder datasetBuilder = new Dataset.Builder();
            datasetBuilder.setValue(viewNode.getAutofillId(), AutofillValue.forText(value), presentation);

            fillResponseBuilder.addDataset(datasetBuilder.build());
        }
    }

    public boolean contains(String[] arr, String query) {
        for(String s : arr) {
            if(s.toLowerCase().equals(query.toLowerCase()))
                return true;
        }
        return false;
    }

    public String inputType(String[] arr) {
        if(contains(arr, "username") || contains(arr, "user") || contains(arr, "email") || contains(arr, "e-mail"))
            return "username";
        else if(contains(arr, "password") || contains(arr, "pass") || contains(arr, "pin"))
            return "password";
        else
            return "unknown";
    }
}
