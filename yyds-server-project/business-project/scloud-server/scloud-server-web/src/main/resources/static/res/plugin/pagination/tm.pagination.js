angular.module("tm.pagination",[]).directive("tmPagination",[function(){return{restrict:"EA",template:'<div class="page-list pagination"><ul class="pagination" ng-show="conf.totalItems > 0"><li ng-class="{disabled: conf.currentPage == 1}" ng-click="prevPage()"><a>&laquo;</a></li><li ng-repeat="item in pageList track by $index" ng-class="{active: item == conf.currentPage, separate: item == \'...\'}" ng-click="changeCurrentPage(item)"><a ng-bind="item"></a></li><li ng-class="{disabled: conf.currentPage == conf.numberOfPages}" ng-click="nextPage()"><a>&raquo;</a></li></ul><div class="page-total" ng-show="conf.totalItems > 0">每页 <select ng-model="conf._itemsPerPage" ng-change="handleChangePageSize()" ng-options="option for option in conf.perPageOptions "></select> 条 共<strong ng-bind="conf.totalItems"></strong>条 </div></div>',replace:!0,scope:{conf:"="},link:function(g,e,n){setTimeout(function(){g.changeCurrentPage=function(e){"..."!=e&&(g.conf.currentPage=e)},g.conf.pagesLength=parseInt(g.conf.pagesLength)?parseInt(g.conf.pagesLength):9,g.conf.pagesLength%2==0&&(g.conf.pagesLength=g.conf.pagesLength-1),g.conf.perPageOptions||(g.conf.perPageOptions=[5,10,15,20,30,50,100]),g.conf._itemsPerPage=g.conf.itemsPerPage,g.prevPage=function(){1<g.conf.currentPage&&(g.conf.currentPage-=1)},g.nextPage=function(){g.conf.currentPage<g.conf.numberOfPages&&(g.conf.currentPage+=1)},g.jumpToPage=function(){g.jumpPageNum=g.jumpPageNum.replace(/[^0-9]/g,""),""!==g.jumpPageNum&&(g.conf.currentPage=g.jumpPageNum)},g.handleChangePageSize=function(){g.conf.itemsPerPage=g.conf._itemsPerPage,g.conf.numberOfPages=Math.ceil(g.conf.totalItems/g.conf.itemsPerPage),g.conf.currentPage<1&&(g.conf.currentPage=1),0<g.conf.numberOfPages&&g.conf.currentPage>g.conf.numberOfPages&&(g.conf.currentPage=g.conf.numberOfPages)},g.$watch(function(){return g.conf.totalItems||(g.conf.totalItems=0),g.conf.totalItems+" "+g.conf.currentPage+" "+g.conf.itemsPerPage},function(e,n){if(g.conf.currentPage=parseInt(g.conf.currentPage)?parseInt(g.conf.currentPage):1,g.conf.totalItems=parseInt(g.conf.totalItems)?parseInt(g.conf.totalItems):0,g.conf.itemsPerPage=parseInt(g.conf.itemsPerPage)?parseInt(g.conf.itemsPerPage):15,g.conf.numberOfPages=Math.ceil(g.conf.totalItems/g.conf.itemsPerPage),g.conf.currentPage<1&&(g.conf.currentPage=1),0<g.conf.numberOfPages&&g.conf.currentPage>g.conf.numberOfPages)g.conf.currentPage=g.conf.numberOfPages;else{g.jumpPageNum=g.conf.currentPage;for(var a,t=g.conf.perPageOptions.length,c=0;c<t;c++)g.conf.perPageOptions[c]==g.conf.itemsPerPage&&(a=!0);if(a||g.conf.perPageOptions.push(g.conf.itemsPerPage),g.conf.perPageOptions.sort(function(e,n){return e-n}),g.pageList=[],g.conf.numberOfPages<=g.conf.pagesLength)for(c=1;c<=g.conf.numberOfPages;c++)g.pageList.push(c);else{var o=(g.conf.pagesLength-1)/2;if(g.conf.currentPage<=o){for(c=1;c<=1+o;c++)g.pageList.push(c);g.pageList.push("..."),g.pageList.push(g.conf.numberOfPages)}else if(g.conf.currentPage>g.conf.numberOfPages-o){for(g.pageList.push(1),g.pageList.push("..."),c=1+o;1<=c;c--)g.pageList.push(g.conf.numberOfPages-c);g.pageList.push(g.conf.numberOfPages)}else{for(g.pageList.push(1),g.pageList.push("..."),c=Math.ceil(o/2);1<=c;c--)g.pageList.push(g.conf.currentPage-c);for(g.pageList.push(g.conf.currentPage),c=1;c<=o/2;c++)g.pageList.push(g.conf.currentPage+c);g.pageList.push("..."),g.pageList.push(g.conf.numberOfPages)}}g.conf.onChange&&(n!=e&&0==n[0]||g.conf.onChange()),g.$parent.conf=g.conf}}),g.$apply()},500)}}}]);