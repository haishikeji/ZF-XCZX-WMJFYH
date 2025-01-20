// otherPage/pages/sunVillageAffairs/manageFramework/index.js
const app = getApp();
import {
  userGetreesbyroot,
  homeGetSonlistByPid,
  assetscateList,
  assetscontentPage,
  homeGetSonById,
  villagesList
} from "../../../../api/sunVillage"
Page({

  /**
   * 页面的初始数据
   */
  data: {
    tabsList: [],
    tabsIndex: 0,
    StatusBarBar: 160 + 'rpx',
    StatusBarBarTwo: 260 + 'rpx',

    pid: 0,
    current: 1,
    hasmore: true,
    list: [],
    cid: '',

    townTxt: '--',
    villageTxt: '--',
    groupTxt: '--',
    tabsNextIndex: 0,
    tabsNextList: [],


    townId: '',
    villageId: '',
    groupId: '',

    routerIndex: '',
    emptyTxt: '数据正在加载中~'
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad(options) {
    let vm = this
    vm.setData({
      pid: options.pid
    })

  },
  onShow(){
    let currentCun = wx.getStorageSync('currentCun')
    let vm = this
    vm.setData({
      townTxt:currentCun.parentName,
      townId: currentCun.parentId,
      villageId: currentCun.deptId,
      villageTxt: currentCun.name,
      groupId: currentCun.groupId || '',
      groupTxt: currentCun.groupName,
      
      current: 1,
      hasmore: true,
      list: [],
    })
    vm.getTabsList()
  },
  // 选择村镇组
  chooseMessage() {
    let userInfo = wx.getStorageSync('userInfo');
    if (userInfo.isCadre == 1) {
      if (userInfo.cadreType == 1 || userInfo.cadreType == 2 || userInfo.cadreType == 3) {
        wx.redirectTo({
          url: '../setVillage/index?index=3&pid=' + this.data.pid,
        })
      } else {
        wx.showToast({
          icon: 'none',
          title: '暂无权限',
        })
        return
      }
    } else {
      wx.showToast({
        icon: 'none',
        title: '暂无权限',
      })
      return
    }
  },
  //返回
  backMessage() {
    wx.redirectTo({
      url: '../index/index',
    })
  },

  // tabs
  getTabsList() {
    let vm = this
    assetscateList({
      pid: vm.data.pid,
      state: 1
    }).then(res => {
      console.log(res.data[0],'res.data');
      vm.setData({
        tabsList: res.data,
        cid: res.data[0].id
      })
      vm.getList()
    })
  },
  //获取列表
  getList() {
    let that = this
    wx.showLoading({
      title: '加载中...',
      mask: true
    })
    var dic = {}
    dic.current = that.data.current;
    dic.size = 20;
    dic.cid = that.data.cid;

    dic.did = that.data.villageId;
    dic.dsid = that.data.groupId;

    assetscontentPage(dic).then(res => {
      wx.hideLoading({
        success: (res) => {},
      })
      if (res.code == 0) {
        var arr = res.data.records
        if (arr.length == 0) {
          that.setData({
            emptyTxt: '暂无数据~'
          })
        }
        arr.forEach(a => {
         
          if (a.assetsContentAutoList) {
            a.assetsContentAutoList.forEach(l => {
              if (l.name == '职务') {
                a.duties = l.val
              }
            })
          }
        })
        if (arr.length < 20) {
          that.setData({
            hasmore: false
          })
        }
        that.setData({
          list: that.data.list.concat(arr),
        })
      } else {
        wx.showToast({
          title: res.msg,
          icon: 'none'
        })
      }
    })
  },
  // tabs点击
  tabsClick(e) {
    let vm = this
    if (e.currentTarget.dataset.index != 0) {
      assetscateList({
        pid: e.currentTarget.dataset.id,
        state: 1
      }).then(res => {
        res.data.unshift({
          name: '全部',
          cid: e.currentTarget.dataset.id,
          id: e.currentTarget.dataset.id
        })
        vm.setData({
          tabsIndex: e.currentTarget.dataset.index,
          tabsNextList: res.data,
          cid: res.data[0].id,
          tabsNextIndex: 0,
          current: 1,
          hasmore: true,
          list: [],
        })
        vm.getList()
      })
    } else {
      vm.setData({
        tabsIndex: e.currentTarget.dataset.index,
        cid: e.currentTarget.dataset.id,
        tabsNextIndex: 0,
        current: 1,
        hasmore: true,
        list: [],
      })
      vm.getList()
    }
  },
  // tabs二级点击
  tabsNextClick(e) {
    this.setData({
      tabsNextIndex: e.currentTarget.dataset.index,
      cid: e.currentTarget.dataset.id,
      current: 1,
      hasmore: true,
      list: [],
    })
    this.getList()
  },
  // 详情
  goDetail(e) {
    wx.navigateTo({
      url: './details?id=' + e.currentTarget.dataset.id + '&pid=' + this.data.pid,
    })
  },


  /**
   * 页面上拉触底事件的处理函数
   */
  onReachBottom() {
    if (this.data.hasmore) {
      this.data.current++
      this.getList()
    }
  },
})