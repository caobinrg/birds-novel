const notice_bar = {
    props:["notice"],
    template:`
           <van-notice-bar left-icon="volume-o" :scrollable="false" mode="closeable">
               <van-swipe
                       vertical
                       class="notice-swipe"
                       :autoplay="3000"
                       :show-indicators="false"
               >
                   <van-swipe-item v-for=" info in notice.info">{{info}}</van-swipe-item>
               </van-swipe>
           </van-notice-bar>
        `
}
