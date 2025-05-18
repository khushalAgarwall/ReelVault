from telegram import Update
from telegram.ext import Application, CommandHandler, MessageHandler, filters, ContextTypes
import os
from dotenv import load_dotenv

# Load environment variables from .env file
load_dotenv()
TOKEN = os.getenv('TELEGRAM_TOKEN')

# Define command handlers
async def start_command(update: Update, context: ContextTypes.DEFAULT_TYPE):
    await update.message.reply_text('Hello! I am your Recipe Saver Bot. Send me a video or link to save recipes!')

async def help_command(update: Update, context: ContextTypes.DEFAULT_TYPE):
    await update.message.reply_text('Send me food videos or links, and I will extract and save recipes for you!')

# Handle video messages
async def handle_video(update: Update, context: ContextTypes.DEFAULT_TYPE):
    await update.message.reply_text("I received your video! Processing recipe extraction...")
    # Here you would add your processing logic
    
# Handle messages with links
async def handle_message(update: Update, context: ContextTypes.DEFAULT_TYPE):
    message_text = update.message.text
    
    # Check if message contains a URL
    if 'http' in message_text:
        await update.message.reply_text("I received your link! Extracting recipe...")
        # Here you would add your link processing logic
    else:
        await update.message.reply_text("Please send me a food video or a link to a food video.")

def main():
    # Create application
    application = Application.builder().token(TOKEN).build()
    
    # Add command handlers
    application.add_handler(CommandHandler('start', start_command))
    application.add_handler(CommandHandler('help', help_command))
    
    # Add message handlers
    application.add_handler(MessageHandler(filters.VIDEO, handle_video))
    application.add_handler(MessageHandler(filters.TEXT, handle_message))
    
    # Run the bot
    print("Bot is running...")
    application.run_polling()

if __name__ == '__main__':
    main()