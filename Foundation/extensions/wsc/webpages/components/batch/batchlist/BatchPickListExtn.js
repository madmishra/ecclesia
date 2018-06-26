
scDefine(["scbase/loader!dojo/_base/declare","scbase/loader!dojo/dom","scbase/loader!extn/components/batch/batchlist/BatchPickListExtnUI"]
,
function(			 
			    _dojodeclare
			 ,_dojodom,
			    _extnBatchPickListExtnUI
){ 
	return _dojodeclare("extn.components.batch.batchlist.BatchPickListExtn", [_extnBatchPickListExtnUI],{
	// custom code here
	onStartWizard : function(){
		console.log('works'); 
	}
	
});
});

