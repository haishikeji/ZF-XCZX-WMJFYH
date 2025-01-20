// otherPage/pages/activity2/detail1.js
import {
  readSzFinanceno,
  infoOutCollect,
  infoOutCollectCancel,
  infoisCollect,
  readSzFinancecate
} from '../../../api/newApi.js';
import WxParse from '../../../wxParse/wxParse';
Page({

  /**
   * 页面的初始数据
   */
  data: {
    id: '',
    isCollect: false,
    cateList:[],

    newList:[]
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad(options) {
    console.log(options)
    if (options.id) {
      this.setData({
        id: options.id,
      })
    }
    this.getCate()
  },
  getCate(){
    readSzFinancecate({
      page: 1,
      size: 999,
    }).then(res=>{
      this.setData({
        cateList:res.data.records
      })
    })
  },

  /**
   * 生命周期函数--监听页面初次渲染完成
   */
  onReady() {
    let t = this;

    if (t.data.id) {
      readSzFinanceno({
        id: t.data.id
      }).then(r => {
        if (r.code == 0) {
          r.data.content ? WxParse.wxParse('rule', 'html', r.data.content || '', t, 0) : '';
          r.data.content1 ? WxParse.wxParse('rule1', 'html', r.data.content1 || '', t, 0) : '';
          r.data.content2 ? WxParse.wxParse('rule2', 'html', r.data.content2 || '', t, 0) : '';
          r.data.content3?WxParse.wxParse('rule3', 'html', r.data.content3 || '', t, 0): '';
          r.data.content4?WxParse.wxParse('rule4', 'html', r.data.content4 || '', t, 0): '';
          // r.data.createTime = r.data.createTime.substring(0, 10);

          let arr = r.data.cates.split(',')
          t.data.cateList.forEach(c=>{
            arr.forEach(a=>{
              if(a==c.title){
                t.data.newList.push(c)
              }
            })
          })
          t.setData({
            d: r.data,
            name: r.data.title,
            newList: t.data.newList
          })
        }
      })
      this.infoisCollect()
    } else {
      wx.showToast({
        icon: 'none',
        title: '未找到文章',
      })
    }
  },

  collect() {
    let role = wx.getStorageSync('role') || 0;
    if (role != 0) {
      wx.showLoading({
        title: '请稍候',
        mask: true
      })
      const fun = this.data.isCollect ? infoOutCollectCancel : infoOutCollect
      const data = {
        infoid: this.data.id,
        outId: 1
      }
      fun(data).then(res => {
        this.infoisCollect()
      })
    } else {
      wx.showModal({
        title: '提示',
        content: '您当前是游客，该操作需要普通用户登录，是否进行普通用户登录？',
        success(res) {
          if (res.confirm) {
            wx.reLaunch({
              url: '/pages/login/login',
            })
          } else if (res.cancel) {
            console.log('用户点击取消')
          }
        }
      })
    }

  },
  infoisCollect() {
    const data = {
      infoid: this.data.id,
      outId: 1
    }
    infoisCollect(data).then(res => {
      this.setData({
        isCollect: res.data
      })

      setTimeout(() => {
        wx.hideLoading();
      }, 500)
    })
  },

  callPhone(e) {
    wx.makePhoneCall({
      phoneNumber: e.mark.phone,
      success(res) {
        console.log('拨打电话成功', res);
      },
      fail(err) {
        console.error('拨打电话失败', err);
      }
    });
  },

  /**
   * 生命周期函数--监听页面显示
   */
  onShow() {

  },

  /**
   * 生命周期函数--监听页面隐藏
   */
  onHide() {

  },

  /**
   * 生命周期函数--监听页面卸载
   */
  onUnload() {

  },

  /**
   * 页面相关事件处理函数--监听用户下拉动作
   */
  onPullDownRefresh() {

  },

  /**
   * 页面上拉触底事件的处理函数
   */
  onReachBottom() {

  },

  /**
   * 用户点击右上角分享
   */
  onShareAppMessage() {

  }
})