package org.support.project.common.bat;

public interface BatListener {
	
	/**
	 * バッチを非同期(別Thread)で実行した際の処理終了後に呼ばれるリスナー
	 * @param result
	 */
	void finish(JobResult result);
	
	
}
