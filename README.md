# 🎙️ WalkieTalk: Because Calling is a Trap and Texting is Labour

Welcome to **WalkieTalk** — a push-to-talk Android app that makes you feel like you're coordinating a heist from a rooftop in Connaught Place, when you're actually just telling your roommate to bring water from the kitchen.

Built by **Badmoosh** (legal name: Abhiraj, used exclusively by recruiters and disappointed relatives), this was my first Android project. Most people start with "Hello World." I started with real-time audio streaming over Firebase because apparently I have a problem with taking things at a normal pace. Zero to sixty. Straight into production. Classic me.

---

## Why Does This App Exist?

Three reasons. All of them valid. None of them negotiable.

**1. Phone calls are psychological warfare.**

You call someone to ask one simple thing. Forty minutes later, you know your cousin's wedding date, your maasi's opinion on your career choices, and somehow you've agreed to attend a function in Kanpur next month that you absolutely cannot afford to go to. Nobody should suffer like this. WalkieTalk keeps it short. You talk. You release. Conversation over. Nobody gets emotionally trapped.

**2. Texting has become a full-time job with no salary.**

First you type. Then autocorrect turns "bhai" into "bah" and you retype. Then you add an emoji to soften the tone. Then someone replies with "k" and you spend the next 20 minutes decoding whether that "k" was passive-aggressive or genuinely just "okay." Then you send a voice note and now the other person has to explain to their entire office why their phone just loudly announced "bhai tune kya khaya aaj." This is madness. There is a better way.

**3. The Walkie-Talkie is just built different.**

Press. Speak. Release. Done. You sound like you're running field operations. There is no cooler way to say "bhai aaja, khana ready hai." Zero wasted words. Maximum personality.

---

## The Tech Stack (No Bakwaas, Only Facts)

- **Java** — Yes, Java. Not Kotlin. I know what you're thinking. But Java has been writing Android apps since before half the "10x developers" on LinkedIn were in school. It works. It ships. Respect the OG.

- **Firebase Realtime Database** — Handles passing audio data between users in real time. Think of it as the *dabbawaala* of the internet — nobody fully understands the internal logistics, the infrastructure looks like it shouldn't work, but somehow the tiffin always arrives hot and on time.

- **AudioRecord & AudioTrack** — This is where the actual engineering lives. Capturing raw PCM audio, buffering it, serializing it to bytes, transmitting it across a network, and reconstructing it on the other side with low enough latency to feel live. Done in my *first* Android project. I am not bragging. I am simply reporting facts.

- **XML Layouts** — Hand-crafted UI. No templates. No copy-paste from StackOverflow (okay, maybe once, but it was for a divider line and I'm not apologising for that).

---

## Features (All Features. Zero Bugs. The Bugs Were Features Too.)

**Push-to-Talk (PTT)**
Hold the button, say your thing, let go. That's it. Simple. Elegant. Powerful. If you mess up what you said mid-transmission, you cannot unsend it. Character building.

**Real-Time Low Latency Audio**
Audio goes from your mouth to their ears fast enough that you'll hear them react before they've even consciously processed what you said. It's that quick.

**Firebase Authentication**
Secure login so that random people can't join your group and overhear your plans. Mainly we didn't want any uninvited *rishtedaars* listening in. You know how they are.

**Runtime Permission Handling**
The app asks for microphone permission politely, only once, and explains why. Like a well-raised child. Unlike some apps that ask for your location, contacts, camera, blood type, and kundli before you've even seen the UI.

---

## A Note on the Architecture (For the Technical Audience)

Real-time audio on mobile is genuinely non-trivial. You're dealing with:

- Audio buffer sizes that affect both latency and stability (too small and you drop frames, too large and you're basically leaving a voicemail)
- Network jitter that makes packets arrive out of order
- Android's audio focus system which will happily interrupt your transmission because someone got a Paytm notification
- Firebase's write limits, which you will absolutely hit if you're not careful about how you're batching audio chunks

I handled all of this in a first project. Not perfectly — no first project is — but it works, it ships, and I learned more building this than I did in an entire semester of Mobile Computing. That semester, by the way, covered none of this.

---

## About the Developer

I'm **Abhiraj Dixit**, going by **Badmoosh** on GitHub because it's accurate and my professional name wasn't available anyway.

I'm a Gen AI architect who accidentally fell into Android development and decided to stay because the problems are interesting. I think deeply about systems, I write code that I'd actually be willing to show people, and I debug with the calm and patience of someone who has already accepted that it's always a missing semicolon.

Things I believe in:
- Ship something real before you ship something perfect
- A README that nobody reads is a README that doesn't deserve to be read
- Biryani is a complete meal and this is not up for debate

---

## Connect

If you want to talk tech, AI, why Java still deserves respect, or literally anything else:

- **LinkedIn:** [Abhiraj Dixit](https://www.linkedin.com/in/abhiraj-dixit-6aa386313/)
- **Portfolio:** [abhiraj-dixit-official.web.app](https://abhiraj-dixit-official.web.app/)
- **Email:** [abhirajdixit25@gmail.com](mailto:abhirajdixit25@gmail.com)

Recruiters: yes, I have a sense of humour. Also yes, I handled real-time audio buffers in my first mobile project. Both things can be true. Reach out.

---

## Final Word

This app was built with genuine curiosity, a lot of late-night debugging sessions, and the unshakeable belief that the best way to learn something is to build something real with it.

If this repo helped you, taught you something, or just made you laugh once — drop a ⭐.

If it didn't, I'll send you a WalkieTalk message at 2am asking why. And you won't be able to hang up.

---

## Desi at Heart, Global by Code 🇮🇳

You know what WalkieTalk and DHURANDHAR have in common?

Both involve one guy with a communication device, deep cover, and absolutely zero intention of being stopped.

Ranveer Singh's Hamza Khan infiltrated an entire criminal empire in Lyari using nothing but wit, guts, and covert ops. I infiltrated the Android ecosystem using Java, Firebase, and three packets of Maggi at 1am. Different missions. Same energy. *Ghayal hoon is liye ghatak hoon.*

SP Chaudhary in the film says things that cannot be repeated in polite company. But the spirit of what he means is this: do the job, take no nonsense, and make sure they remember you were here. That's the philosophy behind this repo too. Minus the Sanjay Dutt threats. Mostly.

And just like R. Madhavan's Ajay Sanyal — the quiet strategist who runs ops from the shadows while everyone else gets the glory — Firebase sits silently in the background of this app, doing all the real work while the UI takes the credit. Agyaat veer. Unsung. Reliable.

This project is built with the same spirit as that film: Made in India, built for the world, and carrying the kind of quiet confidence that doesn't need to announce itself. It just ships.

*Ye Naya Hindustan hai. Ye GitHub mein ghusega bhi, aur star bhi marega.* ⭐

---

**Make in India**
