package com.haolianluo.sms2;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.haolianluo.sms2.model.HStatistics;
import com.haolianluo.sms2.model.HSuggestParser;
import com.lianluo.core.event.IEvent;
import com.lianluo.core.event.ProgressEvent;
import com.lianluo.core.task.BaseTask;
import com.lianluo.core.task.TaskManagerFactory;
import com.lianluo.core.util.ToolsUtil;

public class HProblemActivity extends HActivity {


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.problemfeedback);
		super.onCreate(savedInstanceState);

		final EditText problem = (EditText) findViewById(R.id.problemEdit);
		Button submit = (Button) findViewById(R.id.problemButton);
		submit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				HStatistics statistics = new HStatistics(HProblemActivity.this);
				statistics.add(HStatistics.Z5_16_1, "", "", "");
				if(ToolsUtil.checkNet(HProblemActivity.this)) {
				final String strproblem = problem.getText().toString().trim();
				if(strproblem ==null || "".equals(strproblem)){
					Toast.makeText(HProblemActivity.this, R.string.putInProblem,Toast.LENGTH_SHORT).show();
				}else{
					ProgressEvent event = new ProgressEvent(HProblemActivity.this, getResources().getText(R.string.fk_loading).toString());
					TaskManagerFactory.createParserTaskManager().addTask(new BaseTask(event) {
						@Override
						public void doTask(IEvent event) throws Exception {
							boolean flag = new HSuggestParser(HProblemActivity.this).suggest(strproblem);
							if(flag){
								handler.sendEmptyMessage(0);
							}else{
								handler.sendEmptyMessage(1);
							}
						}
					});
					
				}
			 }else{
				Toast.makeText(HProblemActivity.this, R.string.connect_fail, Toast.LENGTH_SHORT).show();
			 }
			}
		});
		
		Button backbutton = (Button) findViewById(R.id.backButton);
		backbutton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		
	}
	
	private Handler handler = new Handler(){
		@Override
		public void dispatchMessage(Message msg) {
			switch(msg.what){
			case 0:
				Toast.makeText(HProblemActivity.this, R.string.feedbackOk,Toast.LENGTH_SHORT).show();
				finish();
				break;
			case 1:
				Toast.makeText(HProblemActivity.this, R.string.feedbackErr,Toast.LENGTH_SHORT).show();
				break;
			}
			super.dispatchMessage(msg);
		}
	};

}
