// otherPage/pages/sunVillageAffairs/collectiveProperty/index.js
const app = getApp();
import {
  assetscontentPage,
} from "../../../../api/sunVillage"
Page({

  /**
   * 页面的初始数据
   */
  data: {
    StatusBarBar: 250 + 'rpx',
    pid: 0,
    current: 1,
    hasmore: true,
    list: [],


    townTxt: '--',
    villageTxt: '--',
    groupTxt: '--',

    townId: '',
    villageId: '',
    groupId: '',

    emptyTxt: '数据正在加载中~'
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad(options) {
    let vm = this
    vm.setData({
      pid: options.pid,
    })

  },
  onShow() {
    let currentCun = wx.getStorageSync('currentCun')
    let vm = this
    vm.setData({
      townTxt: currentCun.parentName,
      townId: currentCun.parentId,
      villageId: currentCun.deptId,
      villageTxt: currentCun.name,
      groupId: currentCun.groupId || '',
      groupTxt: currentCun.groupName,

      current: 1,
      hasmore: true,
      list: [],
    })
    vm.getList()
  },

  //返回
  backMessage() {
    wx.navigateTo({
      url: '../index/index',
    })
  },

  // 详情
  goDetail(e) {
    wx.navigateTo({
      url: './details?id=' + e.currentTarget.dataset.id
    })
  },
  chooseMessage() {
    let userInfo = wx.getStorageSync('userInfo');
    if (userInfo.isCadre == 1) {
      if (userInfo.cadreType == 1 || userInfo.cadreType == 2 || userInfo.cadreType == 3) {
        wx.redirectTo({
          url: '../setVillage/index?index=1&pid=' + this.data.pid,
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

    dic.cid = that.data.pid;
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
              if (l.name == '地址') {
                a.address = l.val
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
  // 新增
  addProperty() {
    wx.navigateTo({
      url: './add?pid=' + this.data.pid
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