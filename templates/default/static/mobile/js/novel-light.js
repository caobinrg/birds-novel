const novel_light = {
    props:["novel"],
    template:`
        <van-card
            :title="novel.name"
            :desc="novel.intro"
            :thumb="novel.image"
    >
        <template #tags >
            <van-tag plain type="danger" v-for="tag in novel.tags" style="margin-right: 5px">{{tag}}</van-tag>
        </template>
        <template #price>
            <div>{{novel.author}}</div>
        </template>
        <template #num>
        </template >
    </van-card>
        `
}