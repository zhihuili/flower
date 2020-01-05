window.$docsify = {
	name : 'Flower文档',
	repo : 'https://github.com/leeyazhou/test',
	loadSidebar : "_docsify/_sidebar.md",
	loadNavbar : "_docsify/_navbar.md",
	auto2top : true,
	search : 'auto',
	subMaxLevel : 2,
	coverpage : {
		"/" : "_docsify/_coverpage.md"
	},
	toc : {
		scope : '.markdown-section',
		headings : 'h2, h3',
		title : 'Contents',
	},
	footer : {
		copy : '<span>Flower &copy; 2019 ~ 2020</span>',
		auth : '同程艺龙',
		pre : '<hr/>',
		style : 'text-align: middle;'
	},
	plugins : [ EditOnGithubPlugin
			.create('https://github.com/leeyazhou/test/tree/master/docs/') ]
}